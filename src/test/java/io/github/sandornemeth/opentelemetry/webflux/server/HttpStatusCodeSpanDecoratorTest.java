package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;

class HttpStatusCodeSpanDecoratorTest extends SpanDecoratorTestBase {

    @Test
    void testHttpStatusAttribute() {
        testClient.get().uri("/status?code=204")
                .exchange()
                .expectStatus().isEqualTo(204);

        assertThat(lastRecordedSpan())
                .hasKind(SpanKind.SERVER)
                .hasAttributes(Attributes.of(SpanAttributes.HTTP_STATUS_CODE, 204L));
    }

    @Override
    List<WebFluxSpanDecorator> getActiveDecorators() {
        return List.of(new HttpStatusCodeSpanDecorator());
    }
}