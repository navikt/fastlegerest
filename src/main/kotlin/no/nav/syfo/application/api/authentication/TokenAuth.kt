package no.nav.syfo.application.api.authentication

import com.auth0.jwt.JWT

const val JWT_CLAIM_NAVIDENT = "NAVident"
const val JWT_CLAIM_AZP = "azp"

fun getConsumerClientId(token: String): String {
    val decodedJWT = JWT.decode(token)
    return decodedJWT.claims[JWT_CLAIM_AZP]?.asString()
        ?: throw RuntimeException("Missing azp in private claims")
}
