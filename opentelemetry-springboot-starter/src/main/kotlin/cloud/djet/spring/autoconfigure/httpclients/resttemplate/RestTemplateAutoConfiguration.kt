package cloud.djet.spring.autoconfigure.httpclients.resttemplate

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.web.client.RestTemplate
import org.springframework.boot.context.properties.EnableConfigurationProperties
import cloud.djet.spring.autoconfigure.httpclients.HttpClientsProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.beans.factory.ObjectProvider
import io.opentelemetry.api.OpenTelemetry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configures [RestTemplate] for tracing.
 *
 *
 * Adds Open Telemetry instrumentation to RestTemplate beans after initialization
 */
@Configuration
@ConditionalOnClass(RestTemplate::class)
@EnableConfigurationProperties(
    HttpClientsProperties::class
)
@ConditionalOnProperty(prefix = "otel.springboot.httpclients", name = ["enabled"], matchIfMissing = true)
class RestTemplateAutoConfiguration {
    @Bean
    fun otelRestTemplateBeanPostProcessor(openTelemetryProvider: ObjectProvider<OpenTelemetry>): RestTemplateBeanPostProcessor {
        return RestTemplateBeanPostProcessor(openTelemetryProvider)
    }
}