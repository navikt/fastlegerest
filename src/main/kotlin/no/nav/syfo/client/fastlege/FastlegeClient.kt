package no.nav.syfo.client.fastlege

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.syfo.client.azuread.AzureAdClient
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.util.*
import org.slf4j.LoggerFactory

class FastlegeClient(
    private val azureAdClient: AzureAdClient,
    private val fastlegeClientId: String,
    private val fastlegeUrl: String
) {
    private val httpClient = httpClientDefault()

    suspend fun getFastleger(
        personIdent: PersonIdent,
        callId: String,
    ): List<FastlegeProxyDTO> {
        try {
            val token = azureAdClient.getSystemToken(fastlegeClientId)
                ?: throw RuntimeException("Failed to request access to Fastlege: could not get azure token")
            val response = httpClient.get("$fastlegeUrl/api/v1/fastlege") {
                header(HttpHeaders.Authorization, bearerHeader(token.accessToken))
                header(NAV_PERSONIDENT_HEADER, personIdent.value)
                header(NAV_CALL_ID_HEADER, callId)
                accept(ContentType.Application.Json)
            }
            return response.body() ?: emptyList()
        } catch (e: ResponseException) {
            log.error("Request to fastlege failed with status ${e.response.status} and message: ${e.message}")
            throw e
        }
    }

    suspend fun getPraksisInfo(
        herId: Int,
        callId: String,
    ): PraksisInfo? {
        try {
            val token = azureAdClient.getSystemToken(fastlegeClientId)
                ?: throw RuntimeException("Failed to request access to PraksisInfo: could not get azure token")
            val response = httpClient.get("$fastlegeUrl/api/v1/fastlegepraksis/$herId") {
                header(HttpHeaders.Authorization, bearerHeader(token.accessToken))
                header(NAV_CALL_ID_HEADER, callId)
                accept(ContentType.Application.Json)
            }
            return response.body()
        } catch (e: ResponseException) {
            log.error("Request to get praksisinfor for fastlege failed with status ${e.response.status} and message: ${e.message}")
            throw e
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(FastlegeClient::class.java)
    }
}
