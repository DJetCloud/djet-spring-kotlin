package cloud.djet.spring.autoconfigure.propagators

import cloud.djet.spring.autoconfigure.OpenTelemetryAutoConfiguration
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.context.propagation.TextMapPropagator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

internal class PropagationAutoConfigurationTest {
    private val contextRunner: ApplicationContextRunner = ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                OpenTelemetryAutoConfiguration::class.java, PropagationAutoConfiguration::class.java
            )
        )

    @AfterEach
    fun tearDown() {
        GlobalOpenTelemetry.resetForTest()
    }

    @Test
    @DisplayName("when propagation is ENABLED should initialize PropagationAutoConfiguration bean")
    fun shouldBeConfigured() {
        contextRunner
            .withPropertyValues("otel.propagation.enabled=true")
            .run { context ->
                Assertions.assertThat(
                    context.containsBean("propagationAutoConfiguration")
                ).isTrue()
            }
    }

    @Test
    @DisplayName("when propagation is DISABLED should NOT initialize PropagationAutoConfiguration bean")
    fun shouldNotBeConfigured() {
        contextRunner
            .withPropertyValues("otel.propagation.enabled=false")
            .run { context ->
                Assertions.assertThat(
                    context.containsBean("propagationAutoConfiguration")
                ).isFalse()
            }
    }

    @Test
    @DisplayName("when propagation enabled property is MISSING should initialize PropagationAutoConfiguration bean")
    fun noProperty() {
        contextRunner.run { context ->
            Assertions.assertThat(
                context.containsBean("propagationAutoConfiguration")
            ).isTrue()
        }
    }

    @Test
    @DisplayName("when no propagators are defined should contain default propagators")
    fun shouldContainDefaults() {
        contextRunner.run { context ->
            Assertions.assertThat(
                context.getBean("compositeTextMapPropagator", TextMapPropagator::class.java).fields()
            ).contains("traceparent", "baggage")
        }
    }

    @Disabled // TODO add support when require for the DJet project template
    @Test
    @DisplayName("when propagation is set to b3 should contain only b3 propagator")
    fun shouldContainB3() {
        contextRunner
            .withPropertyValues("otel.propagation.type=b3")
            .run { context ->
                val compositePropagator = context.getBean("compositeTextMapPropagator", TextMapPropagator::class.java)
                Assertions.assertThat(compositePropagator.fields())
                    .contains("b3")
                    .doesNotContain("baggage", "traceparent")
            }
    }

    @Test
    @DisplayName("when propagation is set to unsupported value should create an empty propagator")
    fun shouldCreateNoop() {
        contextRunner
            .withPropertyValues("otel.propagation.type=invalid")
            .run { context ->
                val compositePropagator = context.getBean("compositeTextMapPropagator", TextMapPropagator::class.java)
                Assertions.assertThat(compositePropagator.fields()).isEmpty()
            }
    }

    @Disabled // TODO add support when require for the DJet project template
    @Test
    @DisplayName("when propagation is set to some values should contain only supported values")
    fun shouldContainOnlySupported() {
        contextRunner
            .withPropertyValues("otel.propagation.type=invalid,b3")
            .run { context ->
                val compositePropagator = context.getBean("compositeTextMapPropagator", TextMapPropagator::class.java)
                Assertions.assertThat(compositePropagator.fields()).containsExactly("b3")
            }
    }
}