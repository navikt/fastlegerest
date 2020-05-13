package no.nav.syfo.azuread

import no.nav.syfo.metric.Metrikk
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.Instant
import java.util.*

@Component
class AzureAdTokenConsumer(
        private val metrikk: Metrikk,
        @Qualifier("restTemplateWithProxy") private val restTemplateWithProxy: RestTemplate,
        @Value("\${ad.accesstoken.url}") private val url: String,
        @Value("\${client.id}") private val clientId: String,
        @Value("\${client.secret}") private val clientSecret: String
) {
    private val azureAdTokenMap: MutableMap<String, AzureAdResponse> = HashMap()

    fun accessToken(resource: String): String {
        val omToMinutter = Instant.now().plusSeconds(120L)
        val azureAdResponse = azureAdTokenMap[resource]
        if (azureAdResponse == null || azureAdResponse.expires_on.isBefore(omToMinutter)) {
            try {
                val uriString = UriComponentsBuilder.fromHttpUrl(url).toUriString()
                val result = restTemplateWithProxy.exchange(
                        uriString,
                        HttpMethod.POST,
                        entity(resource),
                        AzureAdResponse::class.java
                )
                azureAdTokenMap[resource] = result.body!!
                metrikk.countEvent(CALL_AZUREAD_TOKEN_SYSTEM_SUCCESS)
            } catch (e: RestClientResponseException) {
                LOG.error("Request to get token for Azure AD failed with status " + e.rawStatusCode + " and message: " + e.responseBodyAsString)
                metrikk.countEvent(CALL_AZUREAD_TOKEN_SYSTEM_FAIL)
                throw e
            }
        }
        return azureAdTokenMap[resource]!!.access_token
    }

    fun entity(resource: String): HttpEntity<MultiValueMap<String, String>> {
        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("client_id", clientId)
        body.add("resource", resource)
        body.add("grant_type", "client_credentials")
        body.add("client_secret", clientSecret)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        return HttpEntity(body, headers)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(AzureAdTokenConsumer::class.java)

        private const val CALL_AZUREAD_TOKEN_SYSTEM_BASE = "call_azuread_token_system"
        private const val CALL_AZUREAD_TOKEN_SYSTEM_FAIL = "${CALL_AZUREAD_TOKEN_SYSTEM_BASE}_fail"
        private const val CALL_AZUREAD_TOKEN_SYSTEM_SUCCESS = "${CALL_AZUREAD_TOKEN_SYSTEM_BASE}_success"
    }
}