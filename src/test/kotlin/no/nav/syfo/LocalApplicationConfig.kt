package no.nav.syfo

import no.nav.security.token.support.test.spring.TokenGeneratorConfiguration
import no.nav.syfo.api.exception.RestTemplateErrorHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.*
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate
import java.util.*

@Configuration
@EnableAspectJAutoProxy
@Import(
    TokenGeneratorConfiguration::class
)
@Profile("local")
class LocalApplicationConfig {
    @Bean(name = ["Oidc"])
    @Primary
    fun restTemplate(vararg interceptors: ClientHttpRequestInterceptor): RestTemplate {
        val template = RestTemplate()
        template.interceptors = Arrays.asList(*interceptors)
        template.errorHandler = RestTemplateErrorHandler()
        return template
    }

    @Bean(name = ["BasicAuth"])
    fun basicAuthRestTemplate(
        @Value("\${srv.username}") username: String,
        @Value("\${srv.password}") password: String
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
