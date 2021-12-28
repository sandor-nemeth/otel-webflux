package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.SpanAttributes;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.data.StatusData;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static io.opentelemetry.api.common.AttributeKey.stringKey;
import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests that the {@link StandardSpans} configuration works as expected overall.
 */
class StandardSpansTest extends SpanDecoratorTestBase{

    @Test
    void testPopulatesAllStandardAttributesInSuccess() {
        testClient.get().uri("/hello")
                .header("Prefer", "respond-async, wait=10s")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK);

        assertThat(lastRecordedSpan())
                .hasKind(SpanKind.SERVER)
                .hasStatus(StatusData.ok())
                .hasAttributes(Attributes.of(
                        SpanAttributes.COMPONENT, "Spring WebFlux",
                        SpanAttributes.HTTP_METHOD, "GET",
                        SpanAttributes.HTTP_PATH, "/hello",
                        SpanAttributes.HTTP_PREFER, "respond-async, wait=10s"
                ));
    }

    @Test
    void testHandlesRetryAfter() {
        testClient.get().uri("/retry-after")
                .exchange();

        assertThat(lastRecordedSpan())
                .hasKind(SpanKind.SERVER)
                .hasStatus(StatusData.error())
                .hasAttributes(Attributes.of(
                        SpanAttributes.COMPONENT, "Spring WebFlux",
                        SpanAttributes.HTTP_METHOD, "GET",
                        SpanAttributes.HTTP_PATH, "/retry-after",
                        SpanAttributes.HTTP_STATUS_CODE, 503L,
                        SpanAttributes.HTTP_RETRY_AFTER, "Wed, 21 Oct 2015 07:28:00 GMT"
                ));
    }

    @Test
    void testHandlesErrorStatus() {
        testClient.get().uri("/status?code=500")
                .exchange();

        assertThat(lastRecordedSpan())
                .hasKind(SpanKind.SERVER)
                .hasStatus(StatusData.error())
                .hasAttributes(Attributes.of(
                        SpanAttributes.COMPONENT, "Spring WebFlux",
                        SpanAttributes.HTTP_METHOD, "GET",
                        SpanAttributes.HTTP_PATH, "/status",
                        SpanAttributes.HTTP_STATUS_CODE, 500L
                ));
    }

    @Test
    void test4xxError() {
        testClient.get().uri("/status?code=401")
                .exchange();

        assertThat(lastRecordedSpan())
                .hasKind(SpanKind.SERVER)
                .hasStatus(StatusData.unset())
                .hasAttributes(Attributes.of(
                        SpanAttributes.COMPONENT, "Spring WebFlux",
                        SpanAttributes.HTTP_METHOD, "GET",
                        SpanAttributes.HTTP_PATH, "/status",
                        SpanAttributes.HTTP_STATUS_CODE, 401L
                ));
    }

    @Test
    void testHandlesMethodOverride() {
        testClient.post().uri("/hello-x-http-method-override")
                .header("X-HTTP-Method-Override", "PUT")
                .exchange();

        assertThat(lastRecordedSpan())
                .hasKind(SpanKind.SERVER)
                .hasStatus(StatusData.ok())
                .hasAttributes(Attributes.of(
                        SpanAttributes.COMPONENT, "Spring WebFlux",
                        SpanAttributes.HTTP_METHOD, "POST",
                        SpanAttributes.HTTP_PATH, "/hello-x-http-method-override",
                        SpanAttributes.HTTP_METHOD_OVERRIDE, "PUT"
                ));

    }

    @Test
    void testHandlesException() {
        testClient.get().uri("/exception").exchange();

        SpanData span = lastRecordedSpan();
        assertThat(span)
                .hasKind(SpanKind.SERVER)
                .hasStatus(StatusData.error())
                .hasAttributes(Attributes.of(
                        SpanAttributes.COMPONENT, "Spring WebFlux",
                        SpanAttributes.HTTP_METHOD, "GET",
                        SpanAttributes.HTTP_PATH, "/exception"
                ));

        EventData errorEvent = span.getEvents().get(0);
        assertThat(errorEvent)
                .hasAttributesSatisfying(attrs -> assertThat(attrs)
                        .containsEntry("exception.message", "error message!")
                        .containsEntry("exception.type", RuntimeException.class.getName())
                        .hasEntrySatisfying(stringKey("exception.stacktrace"), (st) -> assertThat(st).isNotBlank()));
    }

    @Override
    List<WebFluxSpanDecorator> getActiveDecorators() {
        return List.of(new StandardSpans());
    }
}