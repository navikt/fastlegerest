package no.nav.syfo.consumer.tilgangskontroll

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.api.auth.OIDCUtil.getConsumerClientIdFraOIDC
import no.nav.syfo.api.auth.OIDCUtil.getNAVIdentFraOIDC
import no.nav.syfo.api.auth.OIDCUtil.tokenFraOIDC
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import no.nav.syfo.util.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.*
import javax.inject.Inject

@Service
class TilgangkontrollConsumer @Inject constructor(
    @Value("\${tilgangskontrollapi.url}") private val syfotilgangskontrollUrl: String,
    @Value("\${syfotilgangskontroll.client.id}") private val syfotilgangskontrollClientId: String,
    @Qualifier("default") private val restTemplate: RestTemplate,
    private val azureAdV2TokenConsumer: AzureAdV2TokenConsumer,
    private val contextHolder: TokenValidationContextHolder
) {
    private val tilgangskontrollPersonUrl: String

    init {
        tilgangskontrollPersonUrl = "$syfotilgangskontrollUrl$TILGANGSKONTROLL_PERSON_PATH"
    }

    fun accessAzureAdV2(fnr: String): Tilgang {
        val token = tokenFraOIDC(contextHolder, OIDCIssuer.VEILEDER_AZURE_V2)
        val veilederId = getNAVIdentFraOIDC(contextHolder)
            ?: throw RuntimeException("Missing veilederId in OIDC-context")
        val azp = getConsumerClientIdFraOIDC(contextHolder)
            ?: throw RuntimeException("Missing azp in OIDC-context")
        val oboToken = azureAdV2TokenConsumer.getOboToken(
            scopeClientId = syfotilgangskontrollClientId,
            token = token,
            veilederId = veilederId,
            azp = azp,
        )
        try {
            return restTemplate.exchange(
                tilgangskontrollPersonUrl,
                HttpMethod.GET,
                lagRequest(
                    personIdentNumber = fnr,
                    token = oboToken,
                ),
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

    private fun lagRequest(
        personIdentNumber: String,
        token: String
    ): HttpEntity<String> {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(token)
        headers[NAV_PERSONIDENT_HEADER] = personIdentNumber
        headers[NAV_CALL_ID_HEADER] = createCallId()
        headers[NAV_CONSUMER_ID_HEADER] = APP_CONSUMER_ID
        return HttpEntity(headers)
    }

    companion object {
        private val log = LoggerFactory.getLogger(TilgangkontrollConsumer::class.java)

        private const val TILGANGSKONTROLL_PERSON_PATH = "/navident/person"

        private val objectMapper = ObjectMapper()
    }
}
