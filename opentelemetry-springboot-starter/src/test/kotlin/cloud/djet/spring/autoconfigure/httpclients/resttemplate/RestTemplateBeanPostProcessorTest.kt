package cloud.djet.spring.autoconfigure.httpclients.resttemplate

import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mock
import org.springframework.beans.factory.ObjectProvider
import io.opentelemetry.api.OpenTelemetry
import org.mockito.Mockito
import io.opentelemetry.instrumentation.spring.httpclients.RestTemplateInterceptor
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate

@ExtendWith(MockitoExtension::class)
internal class RestTemplateBeanPostProcessorTest {
    @Mock
    var openTelemetryProvider: ObjectProvider<OpenTelemetry>? = null
    var restTemplateBeanPostProcessor: RestTemplateBeanPostProcessor? = null
    @BeforeEach
    fun setUp() {
        restTemplateBeanPostProcessor = RestTemplateBeanPostProcessor(openTelemetryProvider!!)
    }

    @Test
    @DisplayName("when processed bean is not of type RestTemplate should return object")
    fun returnsObject() {
        Assertions.assertThat(
            restTemplateBeanPostProcessor!!.postProcessAfterInitialization(
                Any(), "testObject"
            )
        ).isExactlyInstanceOf(Any::class.java)
        Mockito.verifyNoInteractions(openTelemetryProvider)
    }

    @Test
    @DisplayName("when processed bean is of type RestTemplate should return RestTemplate")
    fun returnsRestTemplate() {
        Mockito.`when`(openTelemetryProvider!!.ifUnique).thenReturn(OpenTelemetry.noop())
        Assertions.assertThat(
            restTemplateBeanPostProcessor!!.postProcessAfterInitialization(
                RestTemplate(), "testRestTemplate"
            )
        )
            .isInstanceOf(RestTemplate::class.java)
        Mockito.verify(openTelemetryProvider)?.ifUnique
    }

    @Test
    @DisplayName("when processed bean is of type RestTemplate should add ONE RestTemplateInterceptor")
    fun addsRestTemplateInterceptor() {
        Mockito.`when`(openTelemetryProvider!!.ifUnique).thenReturn(OpenTelemetry.noop())
        val restTemplate = RestTemplate()
        restTemplateBeanPostProcessor!!.postProcessAfterInitialization(restTemplate, "testRestTemplate")
        restTemplateBeanPostProcessor!!.postProcessAfterInitialization(restTemplate, "testRestTemplate")
        restTemplateBeanPostProcessor!!.postProcessAfterInitialization(restTemplate, "testRestTemplate")
        Assertions.assertThat(
            restTemplate.interceptors.stream()
                .filter { rti: ClientHttpRequestInterceptor? -> rti is RestTemplateInterceptor }
                .count())
            .isEqualTo(1)
        Mockito.verify(openTelemetryProvider, Mockito.times(3))?.ifUnique
    }

    @Test
    @DisplayName("when OpenTelemetry is not available should NOT add RestTemplateInterceptor")
    fun doesNotAddRestTemplateInterceptorIfOpenTelemetryUnavailable() {
        Mockito.`when`(openTelemetryProvider!!.ifUnique).thenReturn(null)
        val restTemplate = RestTemplate()
        restTemplateBeanPostProcessor!!.postProcessAfterInitialization(restTemplate, "testRestTemplate")
        restTemplateBeanPostProcessor!!.postProcessAfterInitialization(restTemplate, "testRestTemplate")
        restTemplateBeanPostProcessor!!.postProcessAfterInitialization(restTemplate, "testRestTemplate")
        Assertions.assertThat(
            restTemplate.interceptors.stream()
                .filter { rti: ClientHttpRequestInterceptor? -> rti is RestTemplateInterceptor }
                .count())
            .isEqualTo(0)
        Mockito.verify(openTelemetryProvider, Mockito.times(3))?.ifUnique
    }
}