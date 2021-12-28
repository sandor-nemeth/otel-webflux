package io.github.sandornemeth.opentelemetry.webflux.server;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Subscription;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class TracingWebFilter implements OrderedWebFilter {

    private final int order;
    private final OpenTelemetry openTelemetry;
    private final List<WebFluxSpanDecorator> spanDecorators;

    public TracingWebFilter(
            int order,
            OpenTelemetry openTelemetry,
            List<WebFluxSpanDecorator> spanDecorators) {
        this.order = order;
        this.openTelemetry = openTelemetry;
        this.spanDecorators = spanDecorators;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return new TracingOperator(chain.filter(exchange), exchange, openTelemetry, spanDecorators);
    }

    static class TracingOperator extends MonoOperator<Void, Void> {

        private final ServerWebExchange exchange;
        private final OpenTelemetry openTelemetry;
        private final List<WebFluxSpanDecorator> spanDecorators;

        public TracingOperator(
                Mono<? extends Void> source,
                ServerWebExchange exchange,
                OpenTelemetry openTelemetry,
                List<WebFluxSpanDecorator> spanDecorators) {
            super(source);
            this.exchange = exchange;
            this.openTelemetry = openTelemetry;
            this.spanDecorators = spanDecorators;
        }

        @Override
        public void subscribe(CoreSubscriber<? super Void> actual) {
            Span span = openTelemetry.getTracer("otel", "1.1.0")
                    .spanBuilder("span")
                    .setNoParent()
                    .setSpanKind(SpanKind.SERVER)
                    .startSpan();

            source.subscribe(new TracingSubscriber(exchange, actual, spanDecorators, span));
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    static class TracingSubscriber implements CoreSubscriber<Void> {

        private static final Log LOG = LogFactory.getLog(TracingSubscriber.class);

        private final CoreSubscriber<? super Void> subscriber;
        private final ServerWebExchange exchange;
        private final List<WebFluxSpanDecorator> spanDecorators;
        private final Span span;

        public TracingSubscriber(
                ServerWebExchange exchange,
                CoreSubscriber<? super Void> subscriber,
                List<WebFluxSpanDecorator> spanDecorators,
                Span span) {
            this.exchange = exchange;
            this.subscriber = subscriber;
            this.spanDecorators = spanDecorators;
            this.span = span;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            subscriber.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    spanDecorators.forEach(decorator -> safelyCall(() -> decorator.onRequest(exchange, span)));
                    subscription.request(n);
                }

                @Override
                public void cancel() {
                    spanDecorators.forEach(decorator -> safelyCall(() -> decorator.onCancel(exchange, span)));
                    span.end();
                    subscription.cancel();
                }
            });
        }

        @Override
        public void onNext(Void unused) {
            // never happens
        }

        @Override
        public void onError(Throwable t) {
            spanDecorators.forEach(decorator -> safelyCall(() -> decorator.onError(exchange, t, span)));
            span.end();
            subscriber.onError(t);
        }

        @Override
        public void onComplete() {
            spanDecorators.forEach(decorator -> safelyCall(() -> decorator.onResponse(exchange, span)));
            span.end();
            subscriber.onComplete();
        }

        private void safelyCall(Runnable r) {
            try {
                r.run();
            } catch (RuntimeException e) {
                LOG.error("Cannot execute span decorator: " + e.getMessage(), e);
            }
        }
    }
}
