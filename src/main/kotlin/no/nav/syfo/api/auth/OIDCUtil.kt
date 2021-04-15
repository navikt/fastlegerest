package no.nav.syfo.api.auth

import no.nav.security.token.support.core.context.TokenValidationContextHolder

object OIDCUtil {
    @JvmStatic
    fun tokenFraOIDC(contextHolder: TokenValidationContextHolder, issuer: String): String {
        return contextHolder.tokenValidationContext.getJwtToken(issuer).tokenAsString
    }
}
