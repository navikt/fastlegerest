package testhelper

import no.nav.syfo.consumer.sts.StsToken
import no.nav.syfo.util.basicCredentials
import org.springframework.http.*
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.util.UriComponentsBuilder

const val STS_TOKEN = "123456789"

val stsToken = StsToken(
    access_token = STS_TOKEN,
    token_type = "Bearer",
    expires_in = 3600
)

fun generateStsToken(): StsToken {
    return stsToken.copy()
}

fun mockAndExpectSTSService(
    mockRestServiceServer: MockRestServiceServer,
    stsUrl: String,
    username: String,
    password: String
) {
    val uriString = UriComponentsBuilder.fromHttpUrl(stsUrl)
        .path("/rest/v1/sts/token")
        .queryParam("grant_type", "client_credentials")
        .queryParam("scope", "openid")
        .toUriString()

    val stsToken = generateStsToken()

    val json = "{ \"access_token\" : \"$STS_TOKEN\", \"token_type\" : \"${stsToken.token_type}\", \"expires_in\" : \"${stsToken.expires_in}\" }"
    mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(uriString))
        .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
        .andExpect(MockRestRequestMatchers.header(HttpHeaders.AUTHORIZATION, basicCredentials(username, password)))
        .andRespond(MockRestResponseCreators.withSuccess(json, MediaType.APPLICATION_JSON))
}
