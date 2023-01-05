package no.nav.syfo.client.azuread

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.syfo.application.api.authentication.getConsumerClientId
import no.nav.syfo.application.api.getNAVIdentFromToken
import no.nav.syfo.application.cache.RedisStore
import no.nav.syfo.client.httpClientProxy
import org.slf4j.LoggerFactory

class AzureAdClient(
    private val azureEnvironment: AzureEnvironment,
    private val cache: RedisStore,
) {
    private val httpClient = httpClientProxy()

    suspend fun getOnBehalfOfToken(
        scopeClientId: String,
        token: String,
    ): AzureAdToken? {
        val azp = getConsumerClientId(token)
        val veilederIdent = getNAVIdentFromToken(token)

        val cacheKey = "$veilederIdent-$azp-$scopeClientId"
        val cachedToken: AzureAdToken? = cache.getObject(cacheKey)
        return if (cachedToken?.isExpired() == false) {
            COUNT_CALL_AZUREAD_TOKEN_SYSTEM_CACHE_HIT.increment()
            cachedToken
        } else {
            val scope = "api://$scopeClientId/.default"
            val azureAdTokenResponse = getAccessToken(
                Parameters.build {
                    append("client_id", azureEnvironment.appClientId)
                    append("client_secret", azureEnvironment.appClientSecret)
                    append("client_assertion_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                    append("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                    append("assertion", token)
                    append("scope", scope)
                    append("requested_token_use", "on_behalf_of")
                }
            )

            azureAdTokenResponse?.toAzureAdToken()?.also {
                COUNT_CALL_AZUREAD_TOKEN_SYSTEM_CACHE_MISS.increment()
                cache.setObject(cacheKey, it, 3600)
            }
        }
    }

    suspend fun getSystemToken(scopeClientId: String): AzureAdToken? {
        val cacheKey = "${CACHE_AZUREAD_TOKEN_SYSTEM_KEY_PREFIX}$scopeClientId"
        val cachedToken: AzureAdToken? = cache.getObject(cacheKey)
        return if (cachedToken?.isExpired() == false) {
            COUNT_CALL_AZUREAD_TOKEN_SYSTEM_CACHE_HIT.increment()
            cachedToken
        } else {
            val azureAdTokenResponse = getAccessToken(
                Parameters.build {
                    append("client_id", azureEnvironment.appClientId)
                    append("client_secret", azureEnvironment.appClientSecret)
                    append("grant_type", "client_credentials")
                    append("scope", "api://$scopeClientId/.default")
                }
            )
            azureAdTokenResponse?.toAzureAdToken()?.also {
                COUNT_CALL_AZUREAD_TOKEN_SYSTEM_CACHE_MISS.increment()
                cache.setObject(cacheKey, it, 3600)
            }
        }
    }

    private suspend fun getAccessToken(
        formParameters: Parameters,
    ): AzureAdTokenResponse? {
        return try {
            val response: HttpResponse = httpClient.post(azureEnvironment.openidConfigTokenEndpoint) {
                accept(ContentType.Application.Json)
                setBody(FormDataContent(formParameters))
            }
            response.body<AzureAdTokenResponse>()
        } catch (e: ClientRequestException) {
            handleUnexpectedResponseException(e)
            null
        } catch (e: ServerResponseException) {
            handleUnexpectedResponseException(e)
            null
        }
    }

    private fun handleUnexpectedResponseException(
        responseException: ResponseException,
    ) {
        log.error(
            "Error while requesting AzureAdAccessToken with statusCode=${responseException.response.status.value}",
            responseException
        )
    }

    companion object {
        const val CACHE_AZUREAD_TOKEN_SYSTEM_KEY_PREFIX = "azuread-token-system-"

        private val log = LoggerFactory.getLogger(AzureAdClient::class.java)
    }
}
