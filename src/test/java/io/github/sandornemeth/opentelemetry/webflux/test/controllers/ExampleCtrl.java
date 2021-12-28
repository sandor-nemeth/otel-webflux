package io.github.sandornemeth.opentelemetry.webflux.test.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
public class ExampleCtrl {

    private static final Log LOG = LogFactory.getLog(ExampleCtrl.class);

    public ExampleCtrl() {
        LOG.info("Controller initialized.");
    }

    @GetMapping("/hello")
    public Mono<String> getHello() {
        return sayHello();
    }

    @GetMapping("/hello/{sensitiveName}")
    public Mono<String> getHello(@PathVariable String sensitiveName) {
        return Mono.just("Hello " + sensitiveName);
    }

    @PostMapping(value = "/hello-http-method-override", headers = {"HTTP-Method-Override=PUT"})
    public Mono<String> httpMethodOverrideHello() {
        return sayHello();
    }

    @PostMapping(value = "/hello-x-http-method-override", headers = {"X-HTTP-Method-Override=PUT"})
    public Mono<String> xHttpMethodOverrideHello() {
        return sayHello();
    }

    @GetMapping("/retry-after")
    public ResponseEntity<Mono<String>> getRetryAfter() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Retry-After", "Wed, 21 Oct 2015 07:28:00 GMT")
                .header("Retry-After", "120")
                .body(Mono.just("hello"));
    }

    @GetMapping("/status")
    public ResponseEntity<Void> returnWithStatus(
            @RequestParam(name = "code", required = false) Integer code) {
        HttpStatus responseStatus = Optional.ofNullable(code)
                .map(HttpStatus::valueOf)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(responseStatus).build();
    }

    @GetMapping("/exception")
    public void getException() {
        throw new RuntimeException("error message!");
    }

    private Mono<String> sayHello() {
        return Mono.just("hello");
    }
}
