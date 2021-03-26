package no.nav.syfo

import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile

@SpringBootApplication
@EnableOIDCTokenValidation(ignore = ["org.springframework"])
@Profile("remote")
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
