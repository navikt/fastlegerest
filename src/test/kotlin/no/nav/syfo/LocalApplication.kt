package no.nav.syfo

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile

@SpringBootApplication
@EnableJwtTokenValidation
@Profile("local")
class LocalApplication

fun main(args: Array<String>) {
    runApplication<LocalApplication>(*args)
}
