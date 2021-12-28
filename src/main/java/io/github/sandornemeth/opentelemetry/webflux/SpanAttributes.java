package io.github.sandornemeth.opentelemetry.webflux;

import io.opentelemetry.api.common.AttributeKey;

import static io.opentelemetry.api.common.AttributeKey.longKey;
import static io.opentelemetry.api.common.AttributeKey.stringKey;

public interface SpanAttributes {

    AttributeKey<String> COMPONENT = stringKey("component");

    AttributeKey<String> HTTP_METHOD_OVERRIDE = stringKey("http.method_override");
    AttributeKey<String> HTTP_METHOD = stringKey("http.method");
    AttributeKey<String> HTTP_PATH = stringKey("http.path");
    AttributeKey<String> HTTP_PREFER = stringKey("http.prefer");
    AttributeKey<String> HTTP_RETRY_AFTER = stringKey("http.retry_after");
    AttributeKey<Long> HTTP_STATUS_CODE = longKey("http.status_code");

    AttributeKey<String> PEER_ADDRESS = stringKey("peer.address");
    AttributeKey<String> PEER_HOSTNAME = stringKey("peer.hostname");
    AttributeKey<String> PEER_IPV4 = stringKey("peer.ipv4");
    AttributeKey<String> PEER_IPV6 = stringKey("peer.ipv6");
    AttributeKey<Long> PEER_PORT = longKey("peer.port");
}
