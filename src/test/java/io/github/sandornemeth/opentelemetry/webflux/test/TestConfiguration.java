package io.github.sandornemeth.opentelemetry.webflux.test;

import io.github.sandornemeth.opentelemetry.webflux.server.StandardSpans;
import io.github.sandornemeth.opentelemetry.webflux.server.TracingWebFilter;
import io.github.sandornemeth.opentelemetry.webflux.server.WebFluxSpanDecorator;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class TestConfiguration {

    @Bean
    InMemorySpanExporter spanExporter() {
        return InMemorySpanExporter.create();
    }

    @Bean
    OpenTelemetry openTelemetry(SpanExporter spanExporter) {
        BatchSpanProcessor spanProcessor = BatchSpanProcessor
                .builder(spanExporter)
                .setMaxExportBatchSize(1)
                .setMaxQueueSize(1)
                .setScheduleDelay(Duration.ZERO)
                .build();

        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(spanProcessor)
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .buildAndRegisterGlobal();
    }

    @Bean
    UpdateableWebfluxSpanDecorator spanDecorator() {
        return new UpdateableWebfluxSpanDecorator(new StandardSpans());
    }

    @Bean
    TracingWebFilter tracingWebFilter(OpenTelemetry openTelemetry, List<WebFluxSpanDecorator> decorators) {
        return new TracingWebFilter(Integer.MIN_VALUE, openTelemetry, decorators);
    }
}
