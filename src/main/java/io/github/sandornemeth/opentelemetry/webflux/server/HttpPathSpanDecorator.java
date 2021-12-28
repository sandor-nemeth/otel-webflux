package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.trace.Span;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.web.reactive.HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE;
import static org.springframework.web.reactive.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;

public class HttpPathSpanDecorator implements WebFluxSpanDecorator {

    @Override
    public void onResponse(ServerWebExchange exchange, Span span) {
        tag(exchange, span);
    }

    @Override
    public void onError(ServerWebExchange exchange, Throwable error, Span span) {
        tag(exchange, span);
    }

    private void tag(ServerWebExchange exchange, Span span) {
        Optional
                .ofNullable(exchange.getAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE))
                .filter(HandlerMethod.class::isInstance)
                .map(HandlerMethod.class::cast)
                .flatMap(this::extractPathFromHandlerMethod)
                .or(() -> Optional.ofNullable(exchange.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE)))
                .ifPresent(path -> span.setAttribute(SpanAttributes.HTTP_PATH, path));
    }

    private Optional<String> extractPathFromHandlerMethod(HandlerMethod method) {
        // create a prefix if the controller itself has @RequestMapping
        String pathPrefix = Optional
                .ofNullable(method.getBeanType().getAnnotation(RequestMapping.class))
                .flatMap(this::findFirstPathValue)
                .orElse("");

        return Optional
                .ofNullable(method.getMethodAnnotation(RequestMapping.class))
                .flatMap(this::findFirstPathValue)
                .map(path -> pathPrefix + path);
    }

    private Optional<String> findFirstPathValue(RequestMapping r) {
        return Arrays.stream(r.value()).findFirst();
    }
}
