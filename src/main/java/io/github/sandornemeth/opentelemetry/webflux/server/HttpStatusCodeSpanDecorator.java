package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.trace.Span;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

public class HttpStatusCodeSpanDecorator implements WebFluxSpanDecorator {

    @Override
    public void onResponse(ServerWebExchange exchange, Span span) {
        addStatusAttribute(exchange, span);
    }

    @Override
    public void onError(ServerWebExchange exchange, Throwable error, Span span) {
        addStatusAttribute(exchange, span);
    }

    private void addStatusAttribute(ServerWebExchange exchange, Span span) {
        Optional.ofNullable(exchange.getResponse().getStatusCode())
                .map(HttpStatus::value)
                .ifPresent(it -> span.setAttribute(SpanAttributes.HTTP_STATUS_CODE, it));
    }
}

