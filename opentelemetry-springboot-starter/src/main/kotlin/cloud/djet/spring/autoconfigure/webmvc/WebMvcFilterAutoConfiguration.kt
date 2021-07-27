package cloud.djet.spring.autoconfigure.webmvc

import cloud.djet.spring.autoconfigure.webmvc.filter.HandlerMappingResourceNameFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.web.filter.OncePerRequestFilter
import io.opentelemetry.api.OpenTelemetry
import cloud.djet.spring.autoconfigure.webmvc.filter.WebMvcTracingFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerMapping
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

/** Configures [WebMvcTracingFilter] for tracing.  */
@Configuration
@EnableConfigurationProperties(WebMvcProperties::class)
@ConditionalOnProperty(prefix = "otel.springboot.web", name = ["enabled"], matchIfMissing = true)
@ConditionalOnClass(OncePerRequestFilter::class)
class WebMvcFilterAutoConfiguration {
    @Bean
    fun otelWebMvcTracingFilter(openTelemetry: OpenTelemetry): WebMvcTracingFilter {
        return WebMvcTracingFilter(openTelemetry)
    }

    @Bean
    fun resourceNameFilter(handlerMappings: List<HandlerMapping>): HandlerMappingResourceNameFilter {
        return HandlerMappingResourceNameFilter(handlerMappings.filterIsInstance<RequestMappingHandlerMapping>())
    }

}