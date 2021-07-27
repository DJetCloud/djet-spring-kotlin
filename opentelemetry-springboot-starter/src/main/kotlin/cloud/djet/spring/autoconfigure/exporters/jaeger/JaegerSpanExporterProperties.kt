package cloud.djet.spring.autoconfigure.exporters.jaeger

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * Configuration for [io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter].
 * Get Exporter Service Name
 * Get Exporter Endpoint
 * Get max wait time for Collector to process Span Batches
 */
@ConfigurationProperties(prefix = "otel.exporter.jaeger")
class JaegerSpanExporterProperties {
    var isEnabled = true
    var endpoint: String? = null
    var timeout: Duration? = null
}