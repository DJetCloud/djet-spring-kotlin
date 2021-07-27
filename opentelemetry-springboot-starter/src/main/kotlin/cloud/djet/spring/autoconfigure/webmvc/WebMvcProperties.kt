package cloud.djet.spring.autoconfigure.webmvc

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration for the tracing instrumentation of Spring WebMVC
 *
 *
 * Sets default value of otel.springboot.web.enabled to true if the configuration does not exist
 * in application context
 */
@ConfigurationProperties(prefix = "otel.springboot.web")
class WebMvcProperties {
    var isEnabled = true
}