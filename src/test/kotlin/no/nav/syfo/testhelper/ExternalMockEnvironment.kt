package no.nav.syfo.testhelper

import io.ktor.server.netty.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.cache.RedisStore
import no.nav.syfo.testhelper.mock.*

class ExternalMockEnvironment {
    val applicationState: ApplicationState = testAppState()

    val azureAdMock = AzureAdMock()
    val pdlMock = PdlMock()
    val tilgangskontrollMock = VeilederTilgangskontrollMock()
    val fastlegeMock = FastlegeMock()
    val adresseregisterMock = AdresseregisterMock()

    val externalApplicationMockMap = hashMapOf(
        azureAdMock.name to azureAdMock.server,
        pdlMock.name to pdlMock.server,
        tilgangskontrollMock.name to tilgangskontrollMock.server,
    )

    val environment = testEnvironment(
        azureTokenEndpoint = azureAdMock.url,
        pdlUrl = pdlMock.url,
        istilgangskontrollUrl = tilgangskontrollMock.url,
    )

    val wellKnownInternalAzureAD = wellKnownInternalAzureAD()
    lateinit var redisCache: RedisStore

    protected fun finalize() {
        this.stopExternalMocks()
    }

    companion object {
        private val singletonInstance: ExternalMockEnvironment by lazy {
            ExternalMockEnvironment().also {
                it.startExternalMocks()
            }
        }

        fun getInstance(): ExternalMockEnvironment {
            return singletonInstance
        }
    }
}

fun ExternalMockEnvironment.startExternalMocks() {
    this.externalApplicationMockMap.start()
}

fun ExternalMockEnvironment.stopExternalMocks() {
    this.externalApplicationMockMap.stop()
}

fun HashMap<String, NettyApplicationEngine>.start() {
    this.forEach {
        it.value.start()
    }
}

fun HashMap<String, NettyApplicationEngine>.stop(
    gracePeriodMillis: Long = 1L,
    timeoutMillis: Long = 10L,
) {
    this.forEach {
        it.value.stop(gracePeriodMillis, timeoutMillis)
    }
}
