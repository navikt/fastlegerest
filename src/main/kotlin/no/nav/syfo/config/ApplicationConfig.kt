package no.nav.syfo.config

import no.nav.syfo.api.exception.RestTemplateErrorHandler
import no.nav.syfo.consumer.ws.adresseregister.AdresseregisterConfig
import no.nav.syfo.consumer.ws.fastlege.FastlegeInformasjonConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.*
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate
import java.util.*

@Configuration
@Import(AdresseregisterConfig::class, FastlegeInformasjonConfig::class)
@Profile("remote")
class ApplicationConfig {
    @Bean(name = ["Oidc"])
    fun restTemplate(vararg interceptors: ClientHttpRequestInterceptor?): RestTemplate {
        val template = RestTemplate()
        template.interceptors = Arrays.asList(*interceptors)
        template.errorHandler = RestTemplateErrorHandler()
        return template
    }

    @Bean(name = ["BasicAuth"])
    fun basicAuthRestTemplate(
        @Value("\${srv.username}") username: String?,
        @Value("\${srv.password}") password: String?
    ): RestTemplate {
        return RestTemplateBuilder()
            .basicAuthentication(username, password)
            .build()
    }

    @Bean(name = ["default"])
    fun defaultRestTemplate(): RestTemplate {
        return RestTemplate()
    }
}
