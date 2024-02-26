package no.nav.syfo.client.pdl

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.syfo.client.ClientEnvironment
import no.nav.syfo.client.azuread.*
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.util.*
import org.slf4j.LoggerFactory

class PdlClient(
    private val azureAdClient: AzureAdClient,
    private val clientEnvironment: ClientEnvironment,
) {
    private val httpClient = httpClientDefault()

    suspend fun person(
        personident: PersonIdent,
    ): PdlHentPerson? {
        val query = getPdlQuery("/pdl/hentPerson.graphql")

        val request = PdlRequest(query, Variables(personident.value))
        val token = azureAdClient.getSystemToken(clientEnvironment.clientId)?.accessToken
            ?: throw RuntimeException("PDL-call failed: Could not get system token from AzureAD")

        val response: HttpResponse = httpClient.post(clientEnvironment.baseUrl) {
            setBody(request)
            header(HttpHeaders.ContentType, "application/json")
            header(HttpHeaders.Authorization, bearerHeader(token))
            header(BEHANDLINGSNUMMER_HEADER_KEY, BEHANDLINGSNUMMER_HEADER_VALUE)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val pdlPersonReponse = response.body<PdlPersonResponse>()
                return if (pdlPersonReponse.errors != null && pdlPersonReponse.errors.isNotEmpty()) {
                    pdlPersonReponse.errors.forEach {
                        logger.error("Error while requesting person from PersonDataLosningen: ${it.errorMessage()}")
                    }
                    null
                } else {
                    pdlPersonReponse.data
                }
            }
            else -> {
                logger.error("Request with url: ${clientEnvironment.baseUrl} failed with reponse code ${response.status.value}")
                return null
            }
        }
    }

    private fun getPdlQuery(queryFilePath: String): String {
        return this::class.java.getResource(queryFilePath)
            .readText()
            .replace("[\n\r]", "")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PdlClient::class.java)

        // Se behandlingskatalog https://behandlingskatalog.intern.nav.no/
        // Behandling: Sykefraværsoppfølging: Vurdere behov for oppfølging og rett til sykepenger etter §§ 8-4 og 8-8
        private const val BEHANDLINGSNUMMER_HEADER_KEY = "behandlingsnummer"
        private const val BEHANDLINGSNUMMER_HEADER_VALUE = "B426"
    }
}
