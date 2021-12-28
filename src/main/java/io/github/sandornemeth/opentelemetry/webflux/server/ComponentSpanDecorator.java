package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.trace.Span;
import org.springframework.web.server.ServerWebExchange;

public class ComponentSpanDecorator implements WebFluxSpanDecorator{

    @Override
    public void onRequest(ServerWebExchange exchange, Span span) {
        span.setAttribute(SpanAttributes.COMPONENT, "Spring WebFlux");
    }
}
