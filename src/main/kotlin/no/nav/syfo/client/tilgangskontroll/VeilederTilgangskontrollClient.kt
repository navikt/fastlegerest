package no.nav.syfo.client.tilgangskontroll

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.logstash.logback.argument.StructuredArguments
import no.nav.syfo.client.ClientEnvironment
import no.nav.syfo.client.azuread.AzureAdClient
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.util.*
import org.slf4j.LoggerFactory

class VeilederTilgangskontrollClient(
    private val azureAdClient: AzureAdClient,
    private val clientEnvironment: ClientEnvironment,
) {
    private val httpClient = httpClientDefault()
    private val tilgangskontrollPersonUrl = "${clientEnvironment.baseUrl}$TILGANGSKONTROLL_PERSON_PATH"

    suspend fun hasAccess(
        callId: String,
        personident: PersonIdent,
        token: String,
    ): Boolean {
        val onBehalfOfToken = azureAdClient.getOnBehalfOfToken(
            scopeClientId = clientEnvironment.clientId,
            token = token,
        )?.accessToken ?: throw RuntimeException("Failed to request access to Person: Failed to get OBO token")

        return try {
            val response = httpClient.get(tilgangskontrollPersonUrl) {
                header(HttpHeaders.Authorization, bearerHeader(onBehalfOfToken))
                header(NAV_PERSONIDENT_HEADER, personident.value)
                header(NAV_CALL_ID_HEADER, callId)
                accept(ContentType.Application.Json)
            }
            response.body<Tilgang>().erGodkjent
        } catch (e: ClientRequestException) {
            if (e.response.status != HttpStatusCode.Forbidden) {
                handleUnexpectedResponseException(e.response, callId)
            }
            false
        } catch (e: ServerResponseException) {
            handleUnexpectedResponseException(e.response, callId)
            false
        }
    }

    private fun handleUnexpectedResponseException(
        response: HttpResponse,
        callId: String,
    ) {
        log.error(
            "Error while requesting access to person from istilgangskontroll with {}, {}",
            StructuredArguments.keyValue("statusCode", response.status.value.toString()),
            callId,
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(VeilederTilgangskontrollClient::class.java)

        const val TILGANGSKONTROLL_PERSON_PATH = "/api/tilgang/navident/person"
    }
}
