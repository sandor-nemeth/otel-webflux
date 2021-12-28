package io.github.sandornemeth.opentelemetry.webflux.server;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

public class StatusSpanDecorator implements WebFluxSpanDecorator {

    @Override
    public void onResponse(ServerWebExchange exchange, Span span) {
        setStatus(exchange, span, StatusCode.OK);
    }

    @Override
    public void onError(ServerWebExchange exchange, Throwable error, Span span) {
        setStatus(exchange, span, StatusCode.ERROR);
    }

    private void setStatus(ServerWebExchange exchange, Span span, StatusCode fallback) {
        HttpStatus responseStatus = exchange.getResponse().getStatusCode();

        if (null == responseStatus) {
            span.setStatus(fallback);
        } else if (responseStatus.is5xxServerError()) {
            span.setStatus(StatusCode.ERROR);
        } else if (responseStatus.is4xxClientError()) {
            span.setStatus(StatusCode.UNSET);
        } else {
            span.setStatus(StatusCode.OK);
        }
    }
}
