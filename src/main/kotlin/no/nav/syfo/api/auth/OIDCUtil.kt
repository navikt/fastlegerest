package no.nav.syfo.api.auth

import no.nav.security.oidc.OIDCConstants
import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.security.oidc.context.OIDCValidationContext

object OIDCUtil {
    @JvmStatic
    fun tokenFraOIDC(contextHolder: OIDCRequestContextHolder, issuer: String?): String {
        val context = contextHolder
            .getRequestAttribute(OIDCConstants.OIDC_VALIDATION_CONTEXT) as OIDCValidationContext
        return context.getToken(issuer).idToken
    }
}
