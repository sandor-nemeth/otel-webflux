package io.github.sandornemeth.opentelemetry.webflux.test.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
public class ExampleClassLevelRequestMappingController {

    @GetMapping("/hello")
    public Mono<String> getHello() {
        return Mono.just("hello");
    }
}
