package io.github.sandornemeth.opentelemetry.webflux.server;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static io.opentelemetry.api.common.AttributeKey.stringKey;
import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;

class StaticSpanDecoratorTest extends SpanDecoratorTestBase {

    @Test
    void testAddsCustomStaticAttributes() {
        testClient.get().uri("/hello")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK);

        assertThat(lastRecordedSpan())
                .hasKind(SpanKind.SERVER)
                .hasAttributes(Attributes.of(
                        stringKey("k1"), "v1",
                        stringKey("k2"), "v2"
                ));
    }

    @Override
    List<WebFluxSpanDecorator> getActiveDecorators() {
        return List.of(new StaticSpanDecorator(Map.of("k1", "v1", "k2", "v2")));
    }
}