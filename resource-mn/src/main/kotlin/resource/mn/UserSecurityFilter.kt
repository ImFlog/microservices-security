package resource.mn

import be.looorent.micronaut.security.SecurityFilter
import io.micronaut.http.HttpRequest
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.HttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import org.reactivestreams.Publisher

@Filter(value = [
    "/**"
])
internal class UserSecurityFilter(private val filter: SecurityFilter) : HttpServerFilter {
    override fun doFilter(request: HttpRequest<*>, chain: ServerFilterChain): Publisher<MutableHttpResponse<*>> {
        return filter.doFilter(request, chain)
    }
}