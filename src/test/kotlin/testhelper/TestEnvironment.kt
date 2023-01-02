package testhelper

import no.nav.syfo.application.*
import no.nav.syfo.fastlege.api.system.access.PreAuthorizedClient
import no.nav.syfo.util.configuredJacksonMapper
import java.net.ServerSocket

fun testEnvironment(
    azureTokenEndpoint: String = "azureTokenEndpoint",
    pdlUrl: String = "pdl",
    syfotilgangskontrollUrl: String = "tilgangskontroll",
    isproxyUrl: String = "isproxy",
) = Environment(
    aadAppClient = "appClientId",
    aadAppSecret = "appClientSecret",
    azureAppPreAuthorizedApps = configuredJacksonMapper().writeValueAsString(testAzureAppPreAuthorizedApps),
    aadTokenEndpoint = azureTokenEndpoint,
    azureAppWellKnownUrl = "appWellKnownUrl",
    pdlUrl = pdlUrl,
    pdlClientId = "dev-fss.pdl.pdl-api",
    syfotilgangskontrollUrl = syfotilgangskontrollUrl,
    syfotilgangskontrollClientId = "dev-gcp.teamsykefravr.syfotilgangskontroll",
    isproxyUrl = isproxyUrl,
    isproxyClientId = "dev-fss.teamsykefravr.isproxy",
)

fun testAppState() = ApplicationState(
    alive = true,
    ready = true,
)

fun getRandomPort() = ServerSocket(0).use {
    it.localPort
}

const val testIsdialogmoteClientId = "isdialogmote-client-id"
const val testSyfomodiapersonClientId = "syfomodiaperson-client-id"

val testAzureAppPreAuthorizedApps = listOf(
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:isdialogmote",
        clientId = testIsdialogmoteClientId,
    ),
    PreAuthorizedClient(
        name = "dev-fss:teamsykefravr:syfomodiaperson",
        clientId = testSyfomodiapersonClientId,
    ),
)
