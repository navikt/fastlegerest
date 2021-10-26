package no.nav.syfo.config

import no.nav.syfo.api.exception.RestTemplateErrorHandler
import org.springframework.context.annotation.*
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate
import java.util.*

@Configuration
@Profile("remote")
class ApplicationConfig {
    @Bean(name = ["Oidc"])
    fun restTemplate(vararg interceptors: ClientHttpRequestInterceptor?): RestTemplate {
        val template = RestTemplate()
        template.interceptors = Arrays.asList(*interceptors)
        template.errorHandler = RestTemplateErrorHandler()
        return template
    }

   @Bean(name = ["default"])
    fun defaultRestTemplate(): RestTemplate {
        return RestTemplate()
    }
}
