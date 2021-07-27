package cloud.djet.spring.autoconfigure.httpclients.resttemplate

import cloud.djet.spring.autoconfigure.OpenTelemetryAutoConfiguration
import io.opentelemetry.api.GlobalOpenTelemetry
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.assertj.AssertableApplicationContext
import org.springframework.boot.test.context.runner.ApplicationContextRunner

/** Spring Boot auto configuration test for [RestTemplateAutoConfiguration].  */
internal class RestTemplateAutoConfigurationTest {
    private val contextRunner: ApplicationContextRunner = ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                OpenTelemetryAutoConfiguration::class.java, RestTemplateAutoConfiguration::class.java
            )
        )

    @AfterEach
    fun tearDown() {
        GlobalOpenTelemetry.resetForTest()
    }

    @Test
    @DisplayName("when httpclients are ENABLED should initialize RestTemplateInterceptor bean")
    fun httpClientsEnabled() {
        contextRunner
            .withPropertyValues("otel.springboot.httpclients.enabled=true")
            .run { context ->
                Assertions.assertThat(context.getBean(
                    "otelRestTemplateBeanPostProcessor",
                    RestTemplateBeanPostProcessor::class.java)
                ).isNotNull()
            }
    }

    @Test
    @DisplayName("when httpclients are DISABLED should NOT initialize RestTemplateInterceptor bean")
    fun disabledProperty() {
        contextRunner
            .withPropertyValues("otel.springboot.httpclients.enabled=false")
            .run { context ->
                Assertions.assertThat(context.containsBean("otelRestTemplateBeanPostProcessor")).isFalse()
            }
    }

    @Test
    @DisplayName("when httpclients enabled property is MISSING should initialize RestTemplateInterceptor bean")
    fun noProperty() {
        contextRunner.run { context: AssertableApplicationContext ->
            Assertions.assertThat(context.getBean(
                "otelRestTemplateBeanPostProcessor",
                RestTemplateBeanPostProcessor::class.java)
            ).isNotNull()
        }
    }
}