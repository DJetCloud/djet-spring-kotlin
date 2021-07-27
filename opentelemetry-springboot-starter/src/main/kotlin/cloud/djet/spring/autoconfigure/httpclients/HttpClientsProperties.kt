package cloud.djet.spring.autoconfigure.httpclients

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration for the tracing instrumentation of HTTP clients.
 *
 *
 * Sets default value of otel.springboot.httpclients.enabled to true if the configuration does
 * not exist in application context.
 */
@ConfigurationProperties(prefix = "otel.springboot.httpclients")
class HttpClientsProperties {
    var isEnabled = true
}