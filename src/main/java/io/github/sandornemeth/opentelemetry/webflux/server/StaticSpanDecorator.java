package io.github.sandornemeth.opentelemetry.webflux.server;

import io.opentelemetry.api.trace.Span;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

public class StaticSpanDecorator implements WebFluxSpanDecorator {

    private final Map<String, String> attributes;

    public StaticSpanDecorator(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void onRequest(ServerWebExchange exchange, Span span) {
        attributes.forEach(span::setAttribute);
    }
}
