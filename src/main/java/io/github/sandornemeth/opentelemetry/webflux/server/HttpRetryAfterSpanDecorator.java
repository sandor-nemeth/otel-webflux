package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.trace.Span;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

public class HttpRetryAfterSpanDecorator implements WebFluxSpanDecorator {

    @Override
    public void onResponse(ServerWebExchange exchange, Span span) {
        String retryAfter = exchange.getResponse().getHeaders().getFirst("Retry-After");

        if (null != retryAfter && !retryAfter.isBlank()) {
            span.setAttribute(SpanAttributes.HTTP_RETRY_AFTER, retryAfter);
        }
    }
}
