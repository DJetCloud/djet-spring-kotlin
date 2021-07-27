package cloud.djet.spring.autoconfigure.httpclients.resttemplate

import org.springframework.beans.factory.ObjectProvider
import io.opentelemetry.api.OpenTelemetry
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.web.client.RestTemplate
import org.springframework.http.client.ClientHttpRequestInterceptor
import io.opentelemetry.instrumentation.spring.httpclients.RestTemplateInterceptor

class RestTemplateBeanPostProcessor(private val openTelemetryProvider: ObjectProvider<OpenTelemetry>) :
    BeanPostProcessor {
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean !is RestTemplate) {
            return bean
        }
        val restTemplate = bean
        val openTelemetry = openTelemetryProvider.ifUnique
        if (openTelemetry != null) {
            addRestTemplateInterceptorIfNotPresent(restTemplate, openTelemetry)
        }
        return restTemplate
    }

    companion object {
        private fun addRestTemplateInterceptorIfNotPresent(
            restTemplate: RestTemplate, openTelemetry: OpenTelemetry
        ) {
            val restTemplateInterceptors = restTemplate.interceptors
            if (restTemplateInterceptors.stream()
                    .noneMatch { interceptor: ClientHttpRequestInterceptor? -> interceptor is RestTemplateInterceptor }
            ) {
                restTemplateInterceptors.add(0, RestTemplateInterceptor(openTelemetry))
            }
        }
    }
}