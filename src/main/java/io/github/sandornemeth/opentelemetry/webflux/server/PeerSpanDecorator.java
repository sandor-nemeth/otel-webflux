package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import org.springframework.web.server.ServerWebExchange;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Optional;

public class PeerSpanDecorator implements WebFluxSpanDecorator {
    @Override
    public void onRequest(ServerWebExchange exchange, Span span) {
        InetSocketAddress remoteAddr = exchange.getRequest().getRemoteAddress();

        if (null == remoteAddr) {
            return;
        }

        String host = remoteAddr.getHostString();
        int port = remoteAddr.getPort();

        span.setAttribute(SpanAttributes.PEER_ADDRESS, host + ":" + port);
        span.setAttribute(SpanAttributes.PEER_PORT, port);
        InetAddress ip = remoteAddr.getAddress();

        if (null == ip) {
            span.setAttribute(SpanAttributes.PEER_HOSTNAME, host);
        } else {
            if (!Objects.equals(host, ip.getHostAddress())) {
                span.setAttribute(SpanAttributes.PEER_HOSTNAME, host);
            }

            tagIfMatch(span, Inet4Address.class, SpanAttributes.PEER_IPV4, ip);
            tagIfMatch(span, Inet6Address.class, SpanAttributes.PEER_IPV6, ip);
        }
    }

    private void tagIfMatch(
            final Span span,
            final Class<? extends InetAddress> type,
            final AttributeKey<String> attr,
            final InetAddress address) {

        if (type.isInstance(address)) {
            span.setAttribute(attr, address.getHostAddress());
        }
    }

}
