package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.trace.Span;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

public class HttpPreferSpanDecorator implements WebFluxSpanDecorator {

    @Override
    public void onRequest(ServerWebExchange exchange, Span span) {
        Optional.ofNullable(exchange.getRequest().getHeaders().get("Prefer"))
                .map(it -> String.join(", ", it))
                .ifPresent(it -> span.setAttribute(SpanAttributes.HTTP_PREFER, it));
    }
}
