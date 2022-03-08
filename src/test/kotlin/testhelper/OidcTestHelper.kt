package testhelper

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.test.JwtTokenGenerator
import no.nav.syfo.api.auth.OIDCClaim.JWT_CLAIM_AZP
import no.nav.syfo.api.auth.OIDCIssuer

fun settOIDCValidationContext(
    tokenValidationContextHolder: TokenValidationContextHolder,
    jwt: SignedJWT,
    issuer: String,
) {
    val jwtToken = JwtToken(jwt.serialize())
    val issuerTokenMap: MutableMap<String, JwtToken> = HashMap()
    issuerTokenMap[issuer] = jwtToken
    val tokenValidationContext = TokenValidationContext(issuerTokenMap)
    tokenValidationContextHolder.tokenValidationContext = tokenValidationContext
}

fun logInSystemConsumerClient(
    oidcRequestContextHolder: TokenValidationContextHolder,
    consumerClientId: String = "",
) {
    val claimsSet = JWTClaimsSet.parse("{ \"$JWT_CLAIM_AZP\": \"$consumerClientId\"}")
    val jwt = JwtTokenGenerator.createSignedJWT(claimsSet)
    settOIDCValidationContext(oidcRequestContextHolder, jwt, OIDCIssuer.VEILEDER_AZURE_V2)
}
