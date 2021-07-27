package cloud.djet.spring.autoconfigure

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.context.propagation.ContextPropagators
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor
import io.opentelemetry.sdk.trace.export.SpanExporter
import io.opentelemetry.sdk.trace.samplers.Sampler
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class OpenTelemetryAutoConfiguration(private val env: Environment) {

    companion object {
        private const val probability = 1.0
    }

    @Bean
    @ConditionalOnMissingBean
    fun openTelemetry(
        propagatorsProvider: ObjectProvider<ContextPropagators>,
        spanExportersProvider: ObjectProvider<List<SpanExporter>>
    ): OpenTelemetry {

        val appName = env.getProperty("spring.application.name") ?: "unknown-springboot-app"

        val tracerProviderBuilder =
            SdkTracerProvider.builder()
                .setResource(Resource.create(Attributes.of(
                    AttributeKey.stringKey("service.name"), appName
                )))

        spanExportersProvider.getIfAvailable { emptyList() }
            .stream() // todo SimpleSpanProcessor...is that really what we want here?
            .map { SimpleSpanProcessor.create(it) }
            .forEach { tracerProviderBuilder.addSpanProcessor(it) }

        val tracerProvider = tracerProviderBuilder
            .setSampler(Sampler.traceIdRatioBased(probability))
            .build()
        val propagators = propagatorsProvider.getIfAvailable { ContextPropagators.noop() }

        return OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .setPropagators(propagators)
            .buildAndRegisterGlobal()
    }
}