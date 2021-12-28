package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.trace.Span;
import org.springframework.web.server.ServerWebExchange;

public class HttpMethodSpanDecorator implements WebFluxSpanDecorator{
    @Override
    public void onRequest(ServerWebExchange exchange, Span span) {
        span.setAttribute(SpanAttributes.HTTP_METHOD, exchange.getRequest().getMethodValue());
    }
}
