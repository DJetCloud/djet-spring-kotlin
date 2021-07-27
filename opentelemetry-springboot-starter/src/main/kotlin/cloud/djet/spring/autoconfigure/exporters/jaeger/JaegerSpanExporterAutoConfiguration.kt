package cloud.djet.spring.autoconfigure.exporters.jaeger


import org.springframework.boot.autoconfigure.AutoConfigureBefore
import cloud.djet.spring.autoconfigure.OpenTelemetryAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter
import io.grpc.ManagedChannel
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configures [JaegerGrpcSpanExporter] for tracing.
 *
 *
 * Initializes [JaegerGrpcSpanExporter] bean if bean is missing.
 */
@Configuration
@AutoConfigureBefore(OpenTelemetryAutoConfiguration::class)
@EnableConfigurationProperties(
    JaegerSpanExporterProperties::class
)
@ConditionalOnProperty(prefix = "otel.exporter.jaeger", name = ["enabled"], matchIfMissing = true)
@ConditionalOnClass(
    JaegerGrpcSpanExporter::class, ManagedChannel::class
)
class JaegerSpanExporterAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun otelJaegerSpanExporter(properties: JaegerSpanExporterProperties): JaegerGrpcSpanExporter {
        val builder = JaegerGrpcSpanExporter.builder()
        if (properties.endpoint != null) {
            builder.setEndpoint(properties.endpoint)
        }
        if (properties.timeout != null) {
            builder.setTimeout(properties.timeout)
        }
        return builder.build()
    }
}