package cloud.djet.spring.autoconfigure.propagators

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import cloud.djet.spring.autoconfigure.OpenTelemetryAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.beans.factory.ObjectProvider
import io.opentelemetry.context.propagation.TextMapPropagator
import io.opentelemetry.context.propagation.ContextPropagators
import org.springframework.beans.factory.BeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/** Configures [ContextPropagators] bean for propagation.  */
@Configuration
@EnableConfigurationProperties(PropagationProperties::class)
@AutoConfigureBefore(
    OpenTelemetryAutoConfiguration::class
)
@ConditionalOnProperty(prefix = "otel.propagation", name = ["enabled"], matchIfMissing = true)
class PropagationAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun contextPropagators(propagators: ObjectProvider<List<TextMapPropagator>>): ContextPropagators {
        val mapPropagators = propagators.getIfAvailable { emptyList() }
        return if (mapPropagators.isEmpty()) {
            ContextPropagators.noop()
        } else ContextPropagators.create(TextMapPropagator.composite(mapPropagators))
    }

    @Configuration
    class PropagatorsConfiguration {
        @Bean
        fun compositeTextMapPropagator(beanFactory: BeanFactory, properties: PropagationProperties): TextMapPropagator {
            return CompositeTextMapPropagatorFactory.getCompositeTextMapPropagator(
                beanFactory, properties.type
            )
        }
    }
}