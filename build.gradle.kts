plugins {
    id("java-library")
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // use otel 1.1.0 to be compatible with the Zalando efforts
    implementation(platform("io.opentelemetry:opentelemetry-bom:1.1.0"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.6.2"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.opentelemetry:opentelemetry-api")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.opentelemetry:opentelemetry-sdk-testing")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.awaitility:awaitility:4.1.1")
}

tasks.test {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
