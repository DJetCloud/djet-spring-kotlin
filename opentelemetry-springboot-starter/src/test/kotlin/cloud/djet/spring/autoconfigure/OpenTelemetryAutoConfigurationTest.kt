package cloud.djet.spring.autoconfigure

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.OpenTelemetry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.Bean

/** Spring Boot auto configuration test for [OpenTelemetryAutoConfiguration].  */
internal class OpenTelemetryAutoConfigurationTest {
    @TestConfiguration
    class CustomTracerConfiguration {
        @Bean
        fun customOpenTelemetry(): OpenTelemetry {
            return OpenTelemetry.noop()
        }
    }

    private val contextRunner: ApplicationContextRunner = ApplicationContextRunner()
    @AfterEach
    fun tearDown() {
        GlobalOpenTelemetry.resetForTest()
    }

    @Test
    @DisplayName("when Application Context contains OpenTelemetry bean should NOT initialize openTelemetry")
    fun customTracer() {
        contextRunner
            .withUserConfiguration(CustomTracerConfiguration::class.java)
            .withConfiguration(AutoConfigurations.of(OpenTelemetryAutoConfiguration::class.java))
            .run { context ->
                assertThat(context.containsBean("customOpenTelemetry")).isTrue()
                assertThat(context.containsBean("openTelemetry")).isFalse()
            }
    }

    @Test
    @DisplayName("when Application Context DOES NOT contain OpenTelemetry bean should initialize openTelemetry")
    fun initializeTracer() {
        contextRunner
            .withConfiguration(AutoConfigurations.of(OpenTelemetryAutoConfiguration::class.java))
            .run { context -> assertThat(context.containsBean("openTelemetry")).isTrue() }
    }
}