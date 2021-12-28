package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.common.Attributes;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;

public class HttpRetryAfterSpanDecoratorTest extends SpanDecoratorTestBase {

    @Test
    void testFillsRetryAfterField() {
        testClient.get().uri("/retry-after")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

        assertThat(lastRecordedSpan())
                .hasAttributes(Attributes.of(
                        SpanAttributes.HTTP_RETRY_AFTER,
                        "Wed, 21 Oct 2015 07:28:00 GMT"));
    }

    @Override
    List<WebFluxSpanDecorator> getActiveDecorators() {
        return List.of(new HttpRetryAfterSpanDecorator());
    }
}
