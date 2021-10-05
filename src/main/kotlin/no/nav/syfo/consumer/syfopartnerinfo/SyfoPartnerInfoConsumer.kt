package no.nav.syfo.consumer.syfopartnerinfo

import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import no.nav.syfo.metric.Metrikk
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
class SyfoPartnerInfoConsumer(
    private val azureAdV2TokenConsumer: AzureAdV2TokenConsumer,
    private val metrikk: Metrikk,
    @Qualifier("default") private val restTemplate: RestTemplate,
    @Value("\${syfopartnerinfo.client.id}") private val syfopartnerinfoClientId: String,
    @Value("\${syfopartnerinfo.url}") private val syfopartnerinfoUrl: String
) {
    fun getPartnerId(herId: String): List<PartnerInfoResponse> {
        try {
            val response = restTemplate.exchange(
                "$syfopartnerinfoUrl/api/v2/behandler?herid=$herId",
                HttpMethod.GET,
                entity(),
                object : ParameterizedTypeReference<List<PartnerInfoResponse>>() {}
            )
            LOG.info("Got response from syfopartnerinfo ")
            metrikk.countEvent(CALL_SYFOPARTNERINFO_BEHANDLER_SUCCESS)
            return response.body ?: emptyList()
        } catch (e: RestClientResponseException) {
            LOG.error("Request to syfopartnerinfo failed with status ${e.rawStatusCode} and message: ${e.responseBodyAsString}")
            throw e
        }
    }

    fun entity(): HttpEntity<MultiValueMap<String, String>> {
        val azureADSystemToken = azureAdV2TokenConsumer.getSystemToken(syfopartnerinfoClientId)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.setBearerAuth(azureADSystemToken)

        return HttpEntity(headers)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SyfoPartnerInfoConsumer::class.java)
        private const val CALL_SYFOPARTNERINFO_BEHANDLER_SUCCESS = "call_azuread_token_system_success"
    }

}
