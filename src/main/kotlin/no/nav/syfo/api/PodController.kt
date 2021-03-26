package no.nav.syfo.api

import no.nav.security.oidc.api.Unprotected
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/internal"])
class PodController {
    @Unprotected
    @RequestMapping(value = ["/isAlive"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun isAlive() = "Application is alive!"

    @Unprotected
    @RequestMapping(value = ["/isReady"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun isReady() = "Application is ready!"
}