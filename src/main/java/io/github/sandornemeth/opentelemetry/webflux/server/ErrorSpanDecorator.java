package io.github.sandornemeth.opentelemetry.webflux.server;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ServerWebExchange;

public class ErrorSpanDecorator implements WebFluxSpanDecorator {

    @Override
    public void onResponse(ServerWebExchange exchange, Span span) {
        HttpStatus responseStatus = exchange.getResponse().getStatusCode();
        if (responseStatus != null && responseStatus.is5xxServerError()) {
            recordError(span, new HttpServerErrorException(responseStatus));
        }
    }

    @Override
    public void onError(ServerWebExchange exchange, Throwable error, Span span) {
        recordError(span, error);
    }

    private void recordError(Span span, Throwable error) {
        span.setStatus(StatusCode.ERROR);
        span.recordException(error);
    }
}
