package io.github.sandornemeth.opentelemetry.webflux.server;

import io.github.sandornemeth.opentelemetry.webflux.TestApp;
import io.github.sandornemeth.opentelemetry.webflux.test.UpdateableWebfluxSpanDecorator;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.data.SpanData;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(classes = TestApp.class)
@AutoConfigureWebTestClient(timeout = "PT1H")
public abstract class SpanDecoratorTestBase {

    @Autowired
    WebTestClient testClient;

    @Autowired
    InMemorySpanExporter spanExporter;

    @Autowired
    UpdateableWebfluxSpanDecorator updateableWebfluxSpanDecorator;

    @BeforeEach
    void setup() {
        spanExporter.reset();
        updateableWebfluxSpanDecorator.setActiveSpanDecorators(getActiveDecorators());
    }

    abstract List<WebFluxSpanDecorator> getActiveDecorators();

    SpanData lastRecordedSpan() {
        return spanExporter.getFinishedSpanItems().get(0);
    }
}
