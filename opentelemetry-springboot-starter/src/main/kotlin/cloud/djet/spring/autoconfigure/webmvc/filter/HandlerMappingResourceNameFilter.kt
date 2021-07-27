package cloud.djet.spring.autoconfigure.webmvc.filter

import io.opentelemetry.context.Context
import org.springframework.core.Ordered
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerMapping
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class HandlerMappingResourceNameFilter(private val handlerMappings: List<HandlerMapping>) : OncePerRequestFilter(), Ordered {

	public override fun doFilterInternal(
		request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
	) {
		val context = Context.current()
		if (handlerMappings.isNotEmpty()) {
			try {
				if (findMapping(request)) {
					// Name the parent span based on the matching pattern
					// Let the parent span resource name be set with the attribute set in findMapping.
					ServerNameUpdater.updateServerSpanName(context, request)
				}
			} catch (ignored: Exception) {
				// mapping.getHandler() threw exception. Ignore it.
			}
		}
		filterChain.doFilter(request, response)
	}

	private fun findMapping(request: HttpServletRequest): Boolean {
		for (mapping in handlerMappings) {
			val handler = mapping.getHandler(request)
			if (handler != null) {
				return true
			}
		}
		return false
	}

	override fun destroy() {}
	override fun getOrder(): Int {
		// Run after all HIGHEST_PRECEDENCE items and after WebMvcTracingFilter
		return Ordered.HIGHEST_PRECEDENCE + 2
	}
}
