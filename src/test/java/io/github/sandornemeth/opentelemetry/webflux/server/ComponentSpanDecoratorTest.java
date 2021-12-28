package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;

public class ComponentSpanDecoratorTest extends SpanDecoratorTestBase {

    @Test
    void testComponentAttributeIsDecorated() {
        testClient.get().uri("/hello")
                .exchange()
                .expectStatus().isOk();

        assertThat(lastRecordedSpan())
                .hasKind(SpanKind.SERVER)
                .hasAttributes(Attributes.of(SpanAttributes.COMPONENT, "Spring WebFlux"));
    }

    @Override
    List<WebFluxSpanDecorator> getActiveDecorators() {
        return List.of(new ComponentSpanDecorator());
    }
}
