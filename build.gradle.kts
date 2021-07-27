import java.time.Duration
import nebula.plugin.release.git.opinion.Strategies

plugins {
    id("idea")
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.spring") version "1.5.10" apply false
    id("io.github.gradle-nexus.publish-plugin")
    id("nebula.release")
}

nexusPublishing {
    packageGroup.set("io.opentelemetry")

    repositories {
        sonatype {
            username.set(System.getenv("SONATYPE_USER"))
            password.set(System.getenv("SONATYPE_KEY"))
        }
    }

    connectTimeout.set(Duration.ofMinutes(5))
    clientTimeout.set(Duration.ofMinutes(5))

    transitionCheckOptions {
        // We have many artifacts so Maven Central takes a long time on its compliance checks. This sets
        // the timeout for waiting for the repository to close to a comfortable 50 minutes.
        maxRetries.set(300)
        delayBetween.set(Duration.ofSeconds(10))
    }
}

group = "cloud.djet"
version = "1.0-SNAPSHOT"
description = "Libraries for the DJet template with SpringBoot / Kotlin "

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}