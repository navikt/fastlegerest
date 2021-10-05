package no.nav.syfo.api.auth

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.auth.OIDCClaim.JWT_CLAIM_AZP
import no.nav.syfo.api.auth.OIDCClaim.NAVIDENT

object OIDCUtil {
    @JvmStatic
    fun tokenFraOIDC(contextHolder: TokenValidationContextHolder, issuer: String): String {
        return contextHolder.tokenValidationContext.getJwtToken(issuer).tokenAsString
    }

    fun getNAVIdentFraOIDC(contextHolder: TokenValidationContextHolder): String? {
        val context = contextHolder.tokenValidationContext
        return context.getClaims(OIDCIssuer.VEILEDER_AZURE_V2).getStringClaim(NAVIDENT)
    }

    fun getConsumerClientIdFraOIDC(contextHolder: TokenValidationContextHolder): String? {
        val context = contextHolder.tokenValidationContext
        return context.getClaims(OIDCIssuer.VEILEDER_AZURE_V2).getStringClaim(JWT_CLAIM_AZP)
    }
}
