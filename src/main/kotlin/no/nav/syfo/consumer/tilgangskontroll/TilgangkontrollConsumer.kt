package no.nav.syfo.consumer.tilgangskontroll

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.api.auth.OIDCUtil.tokenFraOIDC
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.*
import org.springframework.web.util.UriComponentsBuilder
import javax.inject.Inject

@Service
class TilgangkontrollConsumer @Inject constructor(
    @Value("\${local_mock}") private val HAR_LOKAL_MOCK: Boolean,
    @Value("\${tilgangskontrollapi.url}") private val TILGANGSKONTROLLAPI_URL: String,
    @Value("\${syfotilgangskontroll.client.id}") private val syfotilgangskontrollClientId: String,
    @Qualifier("default") private val restTemplate: RestTemplate,
    private val azureAdV2TokenConsumer: AzureAdV2TokenConsumer,
    private val contextHolder: TokenValidationContextHolder
) {
    fun sjekkTilgang(fnr: String): Tilgang {
        if (HAR_LOKAL_MOCK) {
            return Tilgang(
                harTilgang = true,
                begrunnelse = ""
            )
        }
        try {
            val token = tokenFraOIDC(contextHolder, OIDCIssuer.AZURE)
            val url = UriComponentsBuilder.fromHttpUrl(tilgangTilBrukerUrl)
                .queryParam("fnr", fnr)
                .toUriString()

            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                lagRequest(token),
                Tilgang::class.java
            ).body!!
        } catch (e: HttpClientErrorException) {
            return if (e.rawStatusCode == 403) {
                objectMapper.readValue(e.responseBodyAsString, Tilgang::class.java)
            } else {
                log.error("Request to get Tilgang from syfo-tilgangskontroll failed with HTTP-status: ${e.rawStatusCode} and ${e.statusText}")
                throw e
            }
        } catch (e: HttpServerErrorException) {
            log.error("Request to get Tilgang from syfo-tilgangskontroll failed with HTTP-status: ${e.rawStatusCode} and ${e.statusText}")
            throw e
        }
    }

    fun accessAzureAdV2(fnr: String): Tilgang {
        val token = tokenFraOIDC(contextHolder, OIDCIssuer.VEILEDER_AZURE_V2)
        val oboToken = azureAdV2TokenConsumer.getToken(
            scopeClientId = syfotilgangskontrollClientId,
            token = token
        )
        try {
            return restTemplate.exchange(
                tilgangTilBrukerV2Url(fnr),
                HttpMethod.GET,
                lagRequest(oboToken),
                Tilgang::class.java
            ).body!!
        } catch (e: HttpClientErrorException) {
            return if (e.rawStatusCode == 403) {
                objectMapper.readValue(e.responseBodyAsString, Tilgang::class.java)
            } else {
                log.error("Request to get Tilgang from syfo-tilgangskontroll failed with HTTP-status: ${e.rawStatusCode} and ${e.statusText}")
                throw e
            }
        } catch (e: HttpServerErrorException) {
            log.error("Request to get Tilgang from syfo-tilgangskontroll failed with HTTP-status: ${e.rawStatusCode} and ${e.statusText}")
            throw e
        }
    }

    fun isVeilederGrantedAccessToSYFOWithAD(issuer: String): Boolean {
        val token = tokenFraOIDC(contextHolder, issuer)
        val response = restTemplate.exchange(
            "$TILGANGSKONTROLLAPI_URL/syfo",
            HttpMethod.GET,
            lagRequest(token),
            String::class.java
        )
        return response.statusCode.is2xxSuccessful
    }

    private fun lagRequest(token: String): HttpEntity<String> {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(token)
        return HttpEntity(headers)
    }

    private val tilgangTilBrukerUrl: String
        get() = TILGANGSKONTROLLAPI_URL + TILGANG_TIL_BRUKER_VIA_AZURE_PATH

    fun tilgangTilBrukerV2Url(fnr: String): String {
        return "$TILGANGSKONTROLLAPI_URL$TILGANG_TIL_BRUKER_VIA_AZURE_V2_PATH/$fnr"
    }

    companion object {
        private val log = LoggerFactory.getLogger(TilgangkontrollConsumer::class.java)

        private const val TILGANG_TIL_BRUKER_VIA_AZURE_V2_PATH = "/navident/bruker"
        private const val TILGANG_TIL_BRUKER_VIA_AZURE_PATH = "/bruker"

        private val objectMapper = ObjectMapper()
    }
}
