package io.github.sandornemeth.opentelemetry.webflux.server;

import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.data.StatusData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.opentelemetry.api.common.AttributeKey.stringKey;
import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;

public class ErrorSpanDecoratorTest extends SpanDecoratorTestBase {

    @Test
    void testErrorWithReturnedResponseCode() {
        testClient.get().uri("/status?code=500")
                .exchange()
                .expectStatus().isEqualTo(500);

        assertThat(lastRecordedSpan())
                .hasStatus(StatusData.error());
    }

    @Test
    void test4xxErrorIsNotRecordedAsError() {
        testClient.get().uri("/status?code=400")
                .exchange()
                .expectStatus().isEqualTo(400);

        assertThat(lastRecordedSpan())
                .hasStatus(StatusData.unset());
    }

    @Test
    void testExceptionInSpan() {
        testClient.get().uri("/exception")
                .exchange()
                .expectStatus().isEqualTo(500);

        SpanData span = lastRecordedSpan();
        EventData errorEvent = span.getEvents().get(0);

        assertThat(span)
                .hasStatus(StatusData.error());

        assertThat(errorEvent)
                .hasAttributesSatisfying(attrs -> assertThat(attrs)
                        .containsEntry("exception.message", "error message!")
                        .containsEntry("exception.type", RuntimeException.class.getName())
                        .hasEntrySatisfying(stringKey("exception.stacktrace"), (st) -> assertThat(st).isNotBlank()));
    }

    @Override
    List<WebFluxSpanDecorator> getActiveDecorators() {
        return List.of(new ErrorSpanDecorator());
    }
}
