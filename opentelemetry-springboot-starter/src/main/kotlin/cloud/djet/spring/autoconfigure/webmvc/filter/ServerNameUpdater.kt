package cloud.djet.spring.autoconfigure.webmvc.filter

import io.opentelemetry.context.Context
import io.opentelemetry.instrumentation.api.servlet.ServerSpanNaming
import io.opentelemetry.instrumentation.api.servlet.ServletContextPath
import org.springframework.web.servlet.HandlerMapping
import javax.servlet.http.HttpServletRequest

object ServerNameUpdater {
	fun updateServerSpanName(context: Context, request: HttpServletRequest) {
		val bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE)
		if (bestMatchingPattern != null) {
			ServerSpanNaming.updateServerSpanName(context, ServerSpanNaming.Source.CONTROLLER) {
				ServletContextPath.prepend(context, bestMatchingPattern.toString())
			}
		}
	}
}
