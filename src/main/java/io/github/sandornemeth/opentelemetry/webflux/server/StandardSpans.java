package io.github.sandornemeth.opentelemetry.webflux.server;

public class StandardSpans extends DelegatingSpanDecorator {

    public StandardSpans() {
        super(new CompositeSpanDecorator(
                new ComponentSpanDecorator(),
                new HttpMethodOverrideSpanDecorator(),
                new HttpMethodSpanDecorator(),
                new HttpPathSpanDecorator(),
                new HttpPreferSpanDecorator(),
                new HttpStatusCodeSpanDecorator(),
                new PeerSpanDecorator()
        ));
    }
}
