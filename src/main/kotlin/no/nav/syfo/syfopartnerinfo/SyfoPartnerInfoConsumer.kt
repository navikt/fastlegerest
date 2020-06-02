package no.nav.syfo.syfopartnerinfo

import no.nav.syfo.azuread.AzureAdTokenConsumer
import no.nav.syfo.metric.Metrikk
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate

@Service
class SyfoPartnerInfoConsumer(
        private val azureAdTokenConsumer: AzureAdTokenConsumer,
        private val metrikk: Metrikk,
        @Qualifier("Oidc") private val restTemplate: RestTemplate,
        @Value("\${syfopartnerinfo.appid}") private val syfoPartnerInfoAppId: String
) {

    fun getPartnerId(herId: String): List<PartnerInfoResponse> {
        try {
            val response = restTemplate.exchange(
                    "$SYFOPARTNERINFO_BASEURL/api/v1/behandler?herid=$herId",
                    HttpMethod.GET,
                    entity(syfoPartnerInfoAppId),
                    object: ParameterizedTypeReference<List<PartnerInfoResponse>>() {}
            )

            metrikk.countEvent(CALL_SYFOPARTNERINFO_BEHANDLER_SUCCESS)
            return response.body!!
        } catch (e: RestClientResponseException) {
            LOG.error("Request to syfopartnerinfo failed with status ${e.rawStatusCode} and message: ${e.responseBodyAsString}")
            throw e
        }
    }

    fun entity(resource: String): HttpEntity<MultiValueMap<String, String>> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.setBearerAuth(azureAdTokenConsumer.accessToken(resource))

        return HttpEntity(headers)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SyfoPartnerInfoConsumer::class.java)
        private const val SYFOPARTNERINFO_BASEURL = "http://syfopartnerinfo"
        private const val CALL_SYFOPARTNERINFO_BEHANDLER_SUCCESS = "call_azuread_token_system_success"
    }

}
