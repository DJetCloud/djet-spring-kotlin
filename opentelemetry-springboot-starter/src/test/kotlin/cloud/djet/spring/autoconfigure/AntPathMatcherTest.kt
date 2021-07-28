package cloud.djet.spring.autoconfigure

import org.junit.jupiter.api.Test
import org.springframework.util.AntPathMatcher
import kotlin.test.assertTrue

class AntPathMatcherTest {

    @Test
    fun `should match request pattern`() {
        val excludeRequests = listOf(
            "**/actuator/health/**",
            "**/swagger-ui/*.js",
            "/**/swagger-ui/*.css",
            "**/swagger-ui/*.png",
            "**/swagger-ui/*.html"
        )
        val antPathMatcher = AntPathMatcher()

        assertTrue(antPathMatcher.match("/**/swagger-ui/*.css", "/swagger-ui/swagger-ui.css"))
        assertTrue(excludeRequests.any { antPathMatcher.match(it, "/swagger-ui/swagger-ui.css")})
    }
}