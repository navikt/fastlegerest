package testhelper

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.test.JwtTokenGenerator
import no.nav.syfo.api.auth.OIDCIssuer
import java.text.ParseException

object OidcTestHelper {
    @JvmStatic
    @Throws(ParseException::class)
    fun loggInnVeilederAzure(tokenValidationContextHolder: TokenValidationContextHolder, veilederIdent: String) {
        val claimsSet = JWTClaimsSet.parse("{\"NAVident\":\"$veilederIdent\"}")
        val jwt = JwtTokenGenerator.createSignedJWT(claimsSet)
        settOIDCValidationContext(tokenValidationContextHolder, jwt, OIDCIssuer.AZURE)
    }

    private fun settOIDCValidationContext(tokenValidationContextHolder: TokenValidationContextHolder, jwt: SignedJWT, issuer: String) {
        val jwtToken = JwtToken(jwt.serialize())
        val issuerTokenMap: MutableMap<String, JwtToken> = HashMap()
        issuerTokenMap[issuer] = jwtToken
        val tokenValidationContext = TokenValidationContext(issuerTokenMap)
        tokenValidationContextHolder.tokenValidationContext = tokenValidationContext
    }

    @JvmStatic
    fun loggUtAlle(tokenValidationContextHolder: TokenValidationContextHolder) {
        tokenValidationContextHolder.tokenValidationContext = null
    }
}
