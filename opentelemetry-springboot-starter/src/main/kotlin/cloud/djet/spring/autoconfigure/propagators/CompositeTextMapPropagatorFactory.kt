package cloud.djet.spring.autoconfigure.propagators

import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator
import io.opentelemetry.context.propagation.TextMapPropagator
import io.opentelemetry.extension.trace.propagation.JaegerPropagator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanFactory
import org.springframework.util.ClassUtils

/** Factory of composite [TextMapPropagator]. Defaults to W3C and BAGGAGE.  */
object CompositeTextMapPropagatorFactory {
    private val logger = LoggerFactory.getLogger(CompositeTextMapPropagatorFactory::class.java)
    fun getCompositeTextMapPropagator(
        beanFactory: BeanFactory, types: List<String?>
    ): TextMapPropagator {
        val propagators: MutableSet<TextMapPropagator> = HashSet()
        for (type in types) {
            when (type) {
//                "b3" -> if (isOnClasspath("io.opentelemetry.extension.trace.propagation.B3Propagator")) {
//                    propagators.add(
//                        beanFactory
//                            .getBeanProvider(B3Propagator::class.java)
//                            .getIfAvailable(B3Propagator::injectingSingleHeader)
//                    )
//                }
//                "b3multi" -> if (isOnClasspath("io.opentelemetry.extension.trace.propagation.B3Propagator")) {
//                    propagators.add(
//                        beanFactory
//                            .getBeanProvider(B3Propagator::class.java)
//                            .getIfAvailable(B3Propagator::injectingMultiHeaders)
//                    )
//                }
                "jaeger" -> if (isOnClasspath("io.opentelemetry.extension.trace.propagation.JaegerPropagator")) {
                    propagators.add(
                        beanFactory
                            .getBeanProvider(JaegerPropagator::class.java)
                            .getIfAvailable(JaegerPropagator::getInstance)
                    )
                }
//                "ottrace" -> if (isOnClasspath("io.opentelemetry.extension.trace.propagation.OtTracerPropagator")) {
//                    propagators.add(
//                        beanFactory
//                            .getBeanProvider(OtTracePropagator::class.java)
//                            .getIfAvailable(OtTracePropagator::getInstance)
//                    )
//                }
//                "xray" -> if (isOnClasspath("io.opentelemetry.extension.aws.AwsXrayPropagator")) {
//                    propagators.add(
//                        beanFactory
//                            .getBeanProvider(AwsXrayPropagator::class.java)
//                            .getIfAvailable(AwsXrayPropagator::getInstance)
//                    )
//                }
                "tracecontext" -> propagators.add(W3CTraceContextPropagator.getInstance())
                "baggage" -> propagators.add(W3CBaggagePropagator.getInstance())
                else -> logger.warn("Unsupported type of propagator: {}", type)
            }
        }
        return TextMapPropagator.composite(propagators)
    }

    private fun isOnClasspath(clazz: String): Boolean {
        return ClassUtils.isPresent(clazz, null)
    }
}