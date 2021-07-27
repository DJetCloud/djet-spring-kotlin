
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

group = "cloud.djet"

dependencies {
    val springBootVersion = "2.5.1"
    val otelBootVersion = "1.4.0-alpha"
    val openTelemetryVersion = "1.4.1"
    api("org.springframework.boot:spring-boot-starter:$springBootVersion")
    api("org.springframework.boot:spring-boot-starter-aop:$springBootVersion")
    implementation("io.opentelemetry.instrumentation:opentelemetry-servlet-javax-common:$otelBootVersion")
    implementation("io.opentelemetry.instrumentation:opentelemetry-spring-web-3.1:$otelBootVersion")
    compileOnly("org.springframework:spring-webmvc:3.1.0.RELEASE")
    compileOnly("javax.servlet:javax.servlet-api:3.1.0")
    implementation("io.opentelemetry:opentelemetry-api:$openTelemetryVersion")
    implementation("io.opentelemetry:opentelemetry-sdk:$openTelemetryVersion")
    implementation("io.opentelemetry:opentelemetry-exporter-jaeger:$openTelemetryVersion")
    implementation("io.opentelemetry:opentelemetry-extension-trace-propagators:$openTelemetryVersion")
    implementation("io.grpc:grpc-netty-shaded:1.39.0")

    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.20.2")
    testImplementation("org.springframework.boot:spring-boot-test:$springBootVersion") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-aop:$springBootVersion")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter:3.11.2")
    testImplementation("io.opentelemetry:opentelemetry-extension-trace-propagators:$openTelemetryVersion")
    testImplementation("org.springframework:spring-test:5.3.9")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    useJUnitPlatform()
}