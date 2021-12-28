package io.github.sandornemeth.opentelemetry.webflux.server;

import io.opentelemetry.api.trace.Span;
import org.springframework.web.server.ServerWebExchange;

public class DelegatingSpanDecorator implements WebFluxSpanDecorator {

    private final WebFluxSpanDecorator delegate;

    public DelegatingSpanDecorator(WebFluxSpanDecorator delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onRequest(ServerWebExchange exchange, Span span) {
        delegate.onRequest(exchange, span);
    }

    @Override
    public void onResponse(ServerWebExchange exchange, Span span) {
        delegate.onResponse(exchange, span);
    }

    @Override
    public void onCancel(ServerWebExchange exchange, Span span) {
        delegate.onCancel(exchange, span);
    }

    @Override
    public void onError(ServerWebExchange exchange, Throwable error, Span span) {
        delegate.onError(exchange, error, span);
    }
}
