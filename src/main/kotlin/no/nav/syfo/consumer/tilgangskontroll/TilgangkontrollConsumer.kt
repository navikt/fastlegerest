package no.nav.syfo.consumer.tilgangskontroll

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.api.auth.OIDCUtil.tokenFraOIDC
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import javax.inject.Inject

@Service
class TilgangkontrollConsumer @Inject constructor(
    @Value("\${tilgangskontrollapi.url}") private val TILGANGSKONTROLLAPI_URL: String,
    @Value("\${local_mock}") private val HAR_LOKAL_MOCK: Boolean,
    @Qualifier("Oidc") private val restTemplate: RestTemplate,
    private val contextHolder: TokenValidationContextHolder
) {
    @Cacheable(value = ["tilgang"])
    fun sjekkTilgang(fnr: String): Tilgang {
        if (HAR_LOKAL_MOCK) {
            return Tilgang(
                harTilgang = true,
                begrunnelse = ""
            )
        }
        val url = UriComponentsBuilder.fromHttpUrl(tilgangTilBrukerUrl)
            .queryParam("fnr", fnr)
            .toUriString()
        try {
            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                lagRequest(OIDCIssuer.AZURE),
                Tilgang::class.java
            ).body!!
        } catch (e: RestClientResponseException) {
            log.error("Request to get Tilgang from syfo-tilgangskontroll failed with HTTP-status: ${e.rawStatusCode} and ${e.statusText}")
            throw e
        }
    }

    fun isVeilederGrantedAccessToSYFOWithAD(): Boolean {
        val response = restTemplate.exchange(
            "$TILGANGSKONTROLLAPI_URL/syfo",
            HttpMethod.GET,
            lagRequest(OIDCIssuer.AZURE),
            String::class.java
        )
        return response.statusCode.is2xxSuccessful
    }

    private fun lagRequest(issuer: String): HttpEntity<String> {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(tokenFraOIDC(contextHolder, issuer))
        return HttpEntity(headers)
    }

    private val tilgangTilBrukerUrl: String
        get() = TILGANGSKONTROLLAPI_URL + TILGANG_TIL_BRUKER_VIA_AZURE_PATH

    companion object {
        private val log = LoggerFactory.getLogger(TilgangkontrollConsumer::class.java)

        private const val TILGANG_TIL_BRUKER_VIA_AZURE_PATH = "/bruker"
    }
}
