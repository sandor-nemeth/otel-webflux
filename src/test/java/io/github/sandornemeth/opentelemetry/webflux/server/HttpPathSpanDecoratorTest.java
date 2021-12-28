package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;

public class HttpPathSpanDecoratorTest extends SpanDecoratorTestBase {
    @Test
    void testGetPathWithVariable() {
        testClient.get().uri("/hello/John")
                .exchange()
                .expectStatus().isOk();

        assertThat(lastRecordedSpan())
                .hasKind(SpanKind.SERVER)
                .hasAttributes(Attributes.of(
                        SpanAttributes.HTTP_PATH, "/hello/{sensitiveName}"));
    }

    @Test
    void testGetPathWithMappingOnControllerClass() {
        testClient.get().uri("/test/hello")
                .exchange()
                .expectStatus().isOk();

        assertThat(lastRecordedSpan())
                .hasAttributes(Attributes.of(
                        SpanAttributes.HTTP_PATH, "/test/hello"));
    }

    @Override
    List<WebFluxSpanDecorator> getActiveDecorators() {
        return List.of(new HttpPathSpanDecorator());
    }
}
