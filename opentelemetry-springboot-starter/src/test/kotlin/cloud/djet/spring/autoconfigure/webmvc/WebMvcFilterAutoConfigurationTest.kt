package cloud.djet.spring.autoconfigure.webmvc

import cloud.djet.spring.autoconfigure.OpenTelemetryAutoConfiguration
import cloud.djet.spring.autoconfigure.webmvc.filter.WebMvcTracingFilter
import io.opentelemetry.api.GlobalOpenTelemetry
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

/** Spring Boot auto configuration test for [WebMvcFilterAutoConfiguration].  */
class WebMvcFilterAutoConfigurationTest {
    private val contextRunner: ApplicationContextRunner = ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                OpenTelemetryAutoConfiguration::class.java, WebMvcFilterAutoConfiguration::class.java
            )
        )

    @AfterEach
    fun tearDown() {
        GlobalOpenTelemetry.resetForTest()
    }

    @Test
    @DisplayName("when web is ENABLED should initialize WebMvcTracingFilter bean")
    fun webEnabled() {
        contextRunner
            .withPropertyValues("otel.springboot.web.enabled=true")
            .run { context ->
                Assertions.assertThat<Any>(
                    context.getBean(
                        "otelWebMvcTracingFilter",
                        WebMvcTracingFilter::class.java
                    )
                )
                    .isNotNull()
            }
    }

    @Test
    @DisplayName("when web is DISABLED should NOT initialize WebMvcTracingFilter bean")
    fun disabledProperty() {
        contextRunner
            .withPropertyValues("otel.springboot.web.enabled=false")
            .run { context ->
                Assertions.assertThat(context.containsBean("otelWebMvcTracingFilter")).isFalse()
            }
    }

    @Test
    @DisplayName("when web property is MISSING should initialize WebMvcTracingFilter bean")
    fun noProperty() {
        contextRunner.run { context ->
            Assertions.assertThat<Any>(context.getBean("otelWebMvcTracingFilter", WebMvcTracingFilter::class.java))
                .isNotNull()
        }
    }
}