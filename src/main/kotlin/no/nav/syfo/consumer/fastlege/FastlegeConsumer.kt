package no.nav.syfo.consumer.fastlege

import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.util.NAV_PERSONIDENT_HEADER
import no.nav.syfo.util.PersonIdent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate

@Service
class FastlegeConsumer(
    private val azureAdV2TokenConsumer: AzureAdV2TokenConsumer,
    private val metrikk: Metrikk,
    @Qualifier("default") private val restTemplate: RestTemplate,
    @Value("\${isproxy.client.id}") private val fastlegeClientId: String,
    @Value("\${isproxy.url}") private val fastlegeUrl: String
) {
    fun getFastleger(personIdent: PersonIdent): List<FastlegeProxyDTO> {
        try {
            val response = restTemplate.exchange(
                "$fastlegeUrl/api/v1/fastlege",
                HttpMethod.GET,
                entity(personIdent),
                object : ParameterizedTypeReference<List<FastlegeProxyDTO>>() {}
            )
            metrikk.countEvent(CALL_FASTLEGE_SUCCESS)
            return response.body ?: emptyList()
        } catch (e: RestClientResponseException) {
            metrikk.countEvent(CALL_FASTLEGE_FAIL)
            LOG.error("Request to fastlege failed with status ${e.rawStatusCode} and message: ${e.responseBodyAsString}")
            throw e
        }
    }

    fun getPraksisInfo(herId: Int): PraksisInfo? {
        try {
            val response = restTemplate.exchange(
                "$fastlegeUrl/api/v1/fastlegepraksis/$herId",
                HttpMethod.GET,
                entity(null),
                object : ParameterizedTypeReference<PraksisInfo>() {}
            )
            metrikk.countEvent(CALL_FASTLEGE_PRAKSIS_SUCCESS)
            return response.body ?: null
        } catch (e: RestClientResponseException) {
            metrikk.countEvent(CALL_FASTLEGE_PRAKSIS_FAIL)
            LOG.error("Request to get praksisinfor for fastlege failed with status ${e.rawStatusCode} and message: ${e.responseBodyAsString}")
            throw e
        }
    }

    fun entity(personIdent: PersonIdent?): HttpEntity<MultiValueMap<String, String>> {
        val azureADSystemToken = azureAdV2TokenConsumer.getSystemToken(fastlegeClientId)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.setBearerAuth(azureADSystemToken)
        if (personIdent != null) {
            headers.add(NAV_PERSONIDENT_HEADER, personIdent.value)
        }

        return HttpEntity(headers)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(FastlegeConsumer::class.java)
        private const val CALL_FASTLEGE_SUCCESS = "call_fastlege_success"
        private const val CALL_FASTLEGE_FAIL = "call_fastlege_fail"
        private const val CALL_FASTLEGE_PRAKSIS_SUCCESS = "call_fastlege_praksis_success"
        private const val CALL_FASTLEGE_PRAKSIS_FAIL = "call_fastlege_praksis_fail"
    }
}
