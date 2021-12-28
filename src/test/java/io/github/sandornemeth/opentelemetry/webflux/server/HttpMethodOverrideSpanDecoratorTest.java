package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.sdk.trace.data.SpanData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;

public class HttpMethodOverrideSpanDecoratorTest extends SpanDecoratorTestBase {

    @Test
    void testExtractsHttpMethodOverride() {
        testClient.post().uri("/hello-http-method-override")
                .header("HTTP-Method-Override", "PUT")
                .exchange()
                .expectStatus().isOk();

        SpanData span = lastRecordedSpan();
        assertThat(span)
                .hasKind(SpanKind.SERVER)
                .hasAttributes(Attributes.of(SpanAttributes.HTTP_METHOD_OVERRIDE, "PUT"));
    }

    @Test
    void testExtractsXHttpMethodOverride() {
        testClient.post().uri("/hello-x-http-method-override")
                .header("X-HTTP-Method-Override", "PUT")
                .exchange()
                .expectStatus().isOk();

        SpanData span = lastRecordedSpan();
        assertThat(span)
                .hasKind(SpanKind.SERVER)
                .hasAttributes(Attributes.of(SpanAttributes.HTTP_METHOD_OVERRIDE, "PUT"));
    }

    @Override
    List<WebFluxSpanDecorator> getActiveDecorators() {
        return List.of(new HttpMethodOverrideSpanDecorator());
    }
}
