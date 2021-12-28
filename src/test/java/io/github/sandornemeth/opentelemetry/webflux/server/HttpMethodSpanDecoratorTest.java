package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;

public class HttpMethodSpanDecoratorTest extends SpanDecoratorTestBase {

    @Test
    void testSetsHttpMethodForRequest() {
        testClient.get().uri("/hello")
                .exchange()
                .expectStatus().isOk();


        assertThat(lastRecordedSpan())
                .hasKind(SpanKind.SERVER)
                .hasAttributes(Attributes.of(SpanAttributes.HTTP_METHOD, "GET"));
    }

    @Override
    List<WebFluxSpanDecorator> getActiveDecorators() {
        return List.of(new HttpMethodSpanDecorator());
    }
}
