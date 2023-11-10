package testhelper

import no.nav.syfo.application.*
import no.nav.syfo.client.ClientEnvironment
import no.nav.syfo.client.ClientsEnvironment
import no.nav.syfo.client.azuread.AzureEnvironment
import no.nav.syfo.fastlege.api.system.access.PreAuthorizedClient
import no.nav.syfo.util.configuredJacksonMapper
import java.net.ServerSocket

fun testEnvironment(
    azureTokenEndpoint: String = "azureTokenEndpoint",
    pdlUrl: String = "pdl",
    istilgangskontrollUrl: String = "tilgangskontroll",
    isproxyUrl: String = "isproxy",
) = Environment(
    azure = AzureEnvironment(
        appClientId = "appClientId",
        appClientSecret = "appClientSecret",
        appPreAuthorizedApps = configuredJacksonMapper().writeValueAsString(testAzureAppPreAuthorizedApps),
        openidConfigTokenEndpoint = azureTokenEndpoint,
        appWellKnownUrl = "appWellKnownUrl",
    ),
    clients = ClientsEnvironment(
        isproxy = ClientEnvironment(
            clientId = "dev-fss.teamsykefravr.isproxy",
            baseUrl = isproxyUrl,
        ),
        pdl = ClientEnvironment(
            clientId = "dev-fss.pdl.pdl-api",
            baseUrl = pdlUrl,
        ),
        istilgangskontroll = ClientEnvironment(
            clientId = "dev-gcp.teamsykefravr.istilgangskontroll",
            baseUrl = istilgangskontrollUrl,
        )
    ),
    redisHost = "localhost",
    redisPort = 6599,
    redisSecret = "password",
)

fun testAppState() = ApplicationState(
    alive = true,
    ready = true,
)

fun getRandomPort() = ServerSocket(0).use {
    it.localPort
}

const val testIsdialogmeldingClientId = "isdialogmelding-client-id"
const val testSyfomodiapersonClientId = "syfomodiaperson-client-id"

val testAzureAppPreAuthorizedApps = listOf(
    PreAuthorizedClient(
        name = "dev-gcp:teamsykefravr:isdialogmelding",
        clientId = testIsdialogmeldingClientId,
    ),
    PreAuthorizedClient(
        name = "dev-fss:teamsykefravr:syfomodiaperson",
        clientId = testSyfomodiapersonClientId,
    ),
)
