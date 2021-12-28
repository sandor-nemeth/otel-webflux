package io.github.sandornemeth.opentelemetry.webflux.server;

import io.opentelemetry.api.trace.Span;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;

public class CompositeSpanDecorator implements WebFluxSpanDecorator {

    private final Iterable<WebFluxSpanDecorator> decorators;

    public CompositeSpanDecorator(WebFluxSpanDecorator... decorators) {
        this(Arrays.asList(decorators));
    }

    public CompositeSpanDecorator(Iterable<WebFluxSpanDecorator> decorators) {
        this.decorators = decorators;
    }

    @Override
    public void onRequest(ServerWebExchange exchange, Span span) {
        decorators.forEach(it -> it.onRequest(exchange, span));
    }

    @Override
    public void onResponse(ServerWebExchange exchange, Span span) {
        decorators.forEach(it -> it.onResponse(exchange, span));
    }

    @Override
    public void onCancel(ServerWebExchange exchange, Span span) {
        decorators.forEach(it -> it.onCancel(exchange, span));
    }

    @Override
    public void onError(ServerWebExchange exchange, Throwable error, Span span) {
        decorators.forEach(it -> it.onError(exchange, error, span));
    }
}
