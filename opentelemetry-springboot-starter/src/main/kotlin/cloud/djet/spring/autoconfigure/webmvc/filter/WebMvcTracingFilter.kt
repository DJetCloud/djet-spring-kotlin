package cloud.djet.spring.autoconfigure.webmvc.filter

import io.opentelemetry.api.OpenTelemetry
import org.springframework.core.Ordered
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.FilterChain

class WebMvcTracingFilter(openTelemetry: OpenTelemetry?) : OncePerRequestFilter(), Ordered {
	private val tracer = SpringWebMvcServerTracer(openTelemetry)

	companion object {
		const val emptySpanName = "doFilterInternal"
	}

	//TODO make it configurable via application.yml: djet.telemetry.exclude
	// for now we exclude actuator/health and swagger-ui(static) endpoints
	private val excludeRequests = listOf(
		"/**/actuator/health/**",
		"/**/swagger-ui/*.js",
		"/**/swagger-ui/*.css",
		"/**/swagger-ui/*.png",
		"/**/swagger-ui/*.html"
	)

	private val antPathMatcher = AntPathMatcher()

	// we disable tracing for requests that is useless for OpenTelemetry
	override fun shouldNotFilter(request: HttpServletRequest): Boolean {
		return excludeRequests.any { antPathMatcher.match(it, request.requestURI) }
	}

	public override fun doFilterInternal(request: HttpServletRequest,
										 response: HttpServletResponse,
										 filterChain: FilterChain) {
		// see HandlerMappingResourceNameFilter to get HandlerMapping name resolving
		val spanName = if (request.requestURI.isNullOrEmpty()) emptySpanName else request.requestURI

		val ctx = tracer.startSpan(request, request, request, spanName)
		try {
			ctx.makeCurrent()
			filterChain.doFilter(request, response)
			tracer.end(ctx, response)
		} catch (t: Throwable) {
			tracer.endExceptionally(ctx, t, response)
			throw t
		}
	}

	override fun destroy() {}
	override fun getOrder(): Int {
		// Run after all HIGHEST_PRECEDENCE items
		return Ordered.HIGHEST_PRECEDENCE + 1
	}
}
