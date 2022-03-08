package no.nav.syfo.fastlege.api.system.access

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.auth.OIDCUtil
import no.nav.syfo.util.configuredJacksonMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.ws.rs.ForbiddenException

@Component
class APIConsumerAccessService(
    private val tokenValidationContextHolder: TokenValidationContextHolder,
    @Value("\${azure.app.pre.authorized.apps}") private val azureAppPreAuthorizedApps: String,
) {
    private val preAuthorizedClientList: List<PreAuthorizedClient> =
        configuredJacksonMapper().readValue(azureAppPreAuthorizedApps)

    fun validateConsumerApplicationAzp(authorizedApplicationNameList: List<String>) {
        val clientIdList = preAuthorizedClientList
            .filter { authorizedApplicationNameList.contains(it.toNamespaceAndApplicationName().applicationName) }
            .map { it.clientId }

        val consumerClientIdAzp = OIDCUtil.getConsumerClientIdFraOIDC(contextHolder = tokenValidationContextHolder)
            ?: throw IllegalArgumentException("Claim AZP was not found in token")
        if (!clientIdList.contains(consumerClientIdAzp)) {
            throw ForbiddenException("Consumer with clientId(azp)=$consumerClientIdAzp is denied access to system API")
        }
    }
}
