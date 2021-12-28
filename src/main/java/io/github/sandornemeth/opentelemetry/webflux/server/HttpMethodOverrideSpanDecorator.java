package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.trace.Span;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Objects;

public class HttpMethodOverrideSpanDecorator implements WebFluxSpanDecorator {

    private static final List<String> METHOD_OVERRIDE_HEADERS = List.of("HTTP-Method-Override", "X-HTTP-Method-Override");

    @Override
    public void onRequest(ServerWebExchange exchange, Span span) {
        HttpHeaders headers = exchange.getRequest().getHeaders();

        METHOD_OVERRIDE_HEADERS.stream()
                .map(headers::getFirst)
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(override -> span.setAttribute(SpanAttributes.HTTP_METHOD_OVERRIDE, override));
    }
}
