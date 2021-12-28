package io.github.sandornemeth.opentelemetry.webflux.test;

import io.github.sandornemeth.opentelemetry.webflux.server.CompositeSpanDecorator;
import io.github.sandornemeth.opentelemetry.webflux.server.WebFluxSpanDecorator;
import io.opentelemetry.api.trace.Span;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

public class UpdateableWebfluxSpanDecorator implements WebFluxSpanDecorator {
    private CompositeSpanDecorator activeSpanDecorator;

    public UpdateableWebfluxSpanDecorator(WebFluxSpanDecorator... decorators) {
        activeSpanDecorator = new CompositeSpanDecorator(decorators);
    }

    public void setActiveSpanDecorators(List<WebFluxSpanDecorator> decorators) {
        activeSpanDecorator = new CompositeSpanDecorator(decorators);
    }

    @Override
    public void onRequest(ServerWebExchange exchange, Span span) {
        activeSpanDecorator.onRequest(exchange, span);
    }

    @Override
    public void onResponse(ServerWebExchange exchange, Span span) {
        activeSpanDecorator.onResponse(exchange, span);
    }

    @Override
    public void onCancel(ServerWebExchange exchange, Span span) {
        activeSpanDecorator.onCancel(exchange, span);
    }

    @Override
    public void onError(ServerWebExchange exchange, Throwable error, Span span) {
        activeSpanDecorator.onError(exchange, error, span);
    }
}
