package no.nav.syfo.consumer.azuread.v2

import org.springframework.beans.factory.annotation.*
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Component
class AzureAdV2TokenConsumer @Autowired constructor(
    @Qualifier("restTemplateWithProxy") private val restTemplateWithProxy: RestTemplate,
    @Value("\${azuread.tenant.id}") private val tenantId: String,
    @Value("\${client.id}") private val clientId: String,
    @Value("\${client.secret}") private val clientSecret: String
) {
    private val azureOauthTokenEndpoint = "https://login.microsoftonline.com/$tenantId/oauth2/v2.0/token"

    fun getOnBehalfOfToken(
        scopeClientId: String,
        token: String
    ): String {
        val response = restTemplateWithProxy.exchange(
            azureOauthTokenEndpoint,
            HttpMethod.POST,
            requestEntity(scopeClientId, token),
            TokenResponse::class.java
        )
        return response.body!!.access_token
    }

    private fun requestEntity(
        scopeClientId: String,
        token: String
    ): HttpEntity<MultiValueMap<String, String>> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("client_id", clientId)
        body.add("client_secret", clientSecret)
        body.add("client_assertion_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
        body.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
        body.add("assertion", token)
        body.add("scope", "api://$scopeClientId/.default")
        body.add("requested_token_use", "on_behalf_of")
        return HttpEntity<MultiValueMap<String, String>>(body, headers)
    }
}
