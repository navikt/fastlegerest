package testhelper

import io.ktor.server.application.*
import no.nav.syfo.application.api.apiModule

fun Application.testApiModule(
    externalMockEnvironment: ExternalMockEnvironment,
) {
    this.apiModule(
        applicationState = externalMockEnvironment.applicationState,
        environment = externalMockEnvironment.environment,
        wellKnownVeilederV2 = externalMockEnvironment.wellKnownInternalAzureAD,
    )
}
