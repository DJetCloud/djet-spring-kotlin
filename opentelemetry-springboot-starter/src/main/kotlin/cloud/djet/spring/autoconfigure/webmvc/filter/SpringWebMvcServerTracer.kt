package cloud.djet.spring.autoconfigure.webmvc.filter

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.context.Context
import io.opentelemetry.instrumentation.api.tracer.HttpServerTracer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import io.opentelemetry.context.propagation.TextMapGetter
import io.opentelemetry.instrumentation.servlet.javax.JavaxHttpServletRequestGetter

internal class SpringWebMvcServerTracer(openTelemetry: OpenTelemetry?) :
    HttpServerTracer<HttpServletRequest, HttpServletResponse, HttpServletRequest, HttpServletRequest>(openTelemetry) {
    override fun peerPort(request: HttpServletRequest): Int {
        return request.remotePort
    }

    override fun peerHostIp(request: HttpServletRequest): String? {
        return request.remoteAddr
    }

    override fun getGetter(): TextMapGetter<HttpServletRequest> {
        return JavaxHttpServletRequestGetter.GETTER
    }

    override fun url(request: HttpServletRequest): String {
        return request.requestURI
    }

    override fun method(request: HttpServletRequest): String {
        return request.method
    }

    override fun requestHeader(httpServletRequest: HttpServletRequest, name: String): String? {
        return httpServletRequest.getHeader(name)
    }

    override fun responseStatus(httpServletResponse: HttpServletResponse): Int {
        return httpServletResponse.status
    }

    override fun attachServerContext(context: Context, request: HttpServletRequest) {
        request.setAttribute(CONTEXT_ATTRIBUTE, context)
    }

    override fun flavor(connection: HttpServletRequest, request: HttpServletRequest): String {
        return connection.protocol
    }

    override fun getServerContext(request: HttpServletRequest): Context? {
        val context = request.getAttribute(CONTEXT_ATTRIBUTE)
        return if (context is Context) context else null
    }

    override fun getInstrumentationName(): String {
        return "djet-cloud:springboot-instrumentation"
    }
}
