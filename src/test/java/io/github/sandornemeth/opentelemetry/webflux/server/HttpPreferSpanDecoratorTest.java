package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.common.Attributes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;

public class HttpPreferSpanDecoratorTest extends SpanDecoratorTestBase {

    @Test
    void testPopulateHttpPrefer() {
        testClient.get().uri("/hello")
                .header("Prefer", "respond-async, wait=10")
                .exchange()
                .expectStatus().isOk();

        assertThat(lastRecordedSpan())
                .hasAttributes(Attributes.of(
                        SpanAttributes.HTTP_PREFER,
                        "respond-async, wait=10"));
    }

    @Test
    void testAddsMultiplePreferHeaders() {
        testClient.get().uri("/hello")
                .header("Prefer", "respond-async, wait=10")
                .header("Prefer", "priority=5")
                .header("Prefer", "Lenient")
                .exchange()
                .expectStatus().isOk();

        assertThat(lastRecordedSpan())
                .hasAttributes(Attributes.of(
                        SpanAttributes.HTTP_PREFER,
                        "respond-async, wait=10, priority=5, Lenient"));
    }

    @Override
    List<WebFluxSpanDecorator> getActiveDecorators() {
        return List.of(new HttpPreferSpanDecorator());
    }
}
