package no.nav.syfo.application.api

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.authentication.*
import no.nav.syfo.application.metric.registerMetricApi
import no.nav.syfo.client.azuread.AzureAdClient
import no.nav.syfo.client.fastlege.FastlegeClient
import no.nav.syfo.client.pdl.PdlClient
import no.nav.syfo.client.tilgangskontroll.VeilederTilgangskontrollClient
import no.nav.syfo.fastlege.FastlegeService
import no.nav.syfo.fastlege.api.registerFastlegeAzureADApi
import no.nav.syfo.fastlege.api.system.registrerFastlegeSystemApi

fun Application.apiModule(
    applicationState: ApplicationState,
    environment: Environment,
    wellKnownVeilederV2: WellKnown,
) {
    installMetrics()
    installCallId()
    installContentNegotiation()
    installJwtAuthentication(
        jwtIssuerList = listOf(
            JwtIssuer(
                acceptedAudienceList = listOf(environment.aadAppClient),
                jwtIssuerType = JwtIssuerType.INTERNAL_AZUREAD,
                wellKnown = wellKnownVeilederV2,
            ),
        ),
    )
    installStatusPages()
    val azureAdV2Client = AzureAdClient(
        azureAppClientId = environment.aadAppClient,
        azureAppClientSecret = environment.aadAppSecret,
        azureTokenEndpoint = environment.aadTokenEndpoint,
    )
    val tilgangskontrollClient = VeilederTilgangskontrollClient(
        azureAdClient = azureAdV2Client,
        syfotilgangskontrollClientId = environment.syfotilgangskontrollClientId,
        tilgangskontrollBaseUrl = environment.syfotilgangskontrollUrl,
    )
    val pdlClient = PdlClient(
        azureAdClient = azureAdV2Client,
        pdlClientId = environment.pdlClientId,
        pdlUrl = environment.pdlUrl,
    )
    val isproxyClient = FastlegeClient(
        azureAdClient = azureAdV2Client,
        fastlegeClientId = environment.isproxyClientId,
        fastlegeUrl = environment.isproxyUrl,
    )
    val fastlegeService = FastlegeService(
        pdlClient = pdlClient,
        fastlegeClient = isproxyClient,
    )

    routing {
        registerPodApi(
            applicationState = applicationState,
        )
        registerMetricApi()
        authenticate(JwtIssuerType.INTERNAL_AZUREAD.name) {
            registerFastlegeAzureADApi(
                fastlegeService = fastlegeService,
                tilgangkontrollClient = tilgangskontrollClient,
            )
            registrerFastlegeSystemApi(
                apiConsumerAccessService = APISystemConsumerAccessService(
                    azureAppPreAuthorizedApps = environment.azureAppPreAuthorizedApps,
                ),
                fastlegeService = fastlegeService,
            )
        }
    }
}
