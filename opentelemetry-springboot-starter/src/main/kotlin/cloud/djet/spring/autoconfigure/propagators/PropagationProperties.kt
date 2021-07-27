package cloud.djet.spring.autoconfigure.propagators

import org.springframework.boot.context.properties.ConfigurationProperties

/** Configuration for propagators.  */
@ConfigurationProperties(prefix = "otel.propagation")
open class PropagationProperties {
    var type = listOf("tracecontext", "baggage", "jaeger")
}