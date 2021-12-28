package io.github.sandornemeth.opentelemetry.webflux.server;

import io.opentelemetry.sdk.trace.data.StatusData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StatusSpanDecoratorTest extends SpanDecoratorTestBase {

    @Test
    void testHandlesOkStatus() {
        testClient.get().uri("/status?code=201")
                .exchange();

        assertThat(lastRecordedSpan()).hasStatus(StatusData.ok());
    }

    @Test
    void testHandlesDefaultStatus() {
        testClient.get().uri("/hello")
                .exchange();

        assertThat(lastRecordedSpan()).hasStatus(StatusData.ok());
    }

    @Test
    void handlesErrorStatus() {
        testClient.get().uri("/status?code=503")
                .exchange();

        assertThat(lastRecordedSpan()).hasStatus(StatusData.error());
    }

    @Test
    void testHandlesException() {
        testClient.get().uri("/exception").exchange();

        assertThat(lastRecordedSpan()).hasStatus(StatusData.error());
    }

    @Test
    void testHandles4xxError() {
        testClient.get().uri("/status?code=401").exchange();

        assertThat(lastRecordedSpan()).hasStatus(StatusData.unset());
    }

    @Override
    List<WebFluxSpanDecorator> getActiveDecorators() {
        return List.of(new StatusSpanDecorator());
    }
}