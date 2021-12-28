package io.github.sandornemeth.opentelemetry.webflux.server;

import io.opentelemetry.api.trace.Span;
import org.springframework.web.server.ServerWebExchange;

public interface WebFluxSpanDecorator {

    default void onRequest(ServerWebExchange exchange, Span span) {};
    default void onResponse(ServerWebExchange exchange, Span span) {};
    default void onCancel(ServerWebExchange exchange, Span span) {};
    default void onError(ServerWebExchange exchange, Throwable error, Span span) {};

}
