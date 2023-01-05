package no.nav.syfo.application.api

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.Environment
import no.nav.syfo.application.api.authentication.*
import no.nav.syfo.application.cache.RedisStore
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
    wellKnownAzure: WellKnown,
    cache: RedisStore,
) {
    installMetrics()
    installCallId()
    installContentNegotiation()
    installJwtAuthentication(
        jwtIssuerList = listOf(
            JwtIssuer(
                acceptedAudienceList = listOf(environment.azure.appClientId),
                jwtIssuerType = JwtIssuerType.INTERNAL_AZUREAD,
                wellKnown = wellKnownAzure,
            ),
        ),
    )
    installStatusPages()
    val azureAdClient = AzureAdClient(
        azureEnvironment = environment.azure,
        cache = cache,
    )
    val tilgangskontrollClient = VeilederTilgangskontrollClient(
        azureAdClient = azureAdClient,
        clientEnvironment = environment.clients.syfotilgangskontroll,
    )
    val pdlClient = PdlClient(
        azureAdClient = azureAdClient,
        clientEnvironment = environment.clients.pdl,
    )
    val isproxyClient = FastlegeClient(
        azureAdClient = azureAdClient,
        clientEnvironment = environment.clients.isproxy,
    )
    val fastlegeService = FastlegeService(
        pdlClient = pdlClient,
        fastlegeClient = isproxyClient,
        cache = cache,
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
                    azureAppPreAuthorizedApps = environment.azure.appPreAuthorizedApps,
                ),
                fastlegeService = fastlegeService,
            )
        }
    }
}
