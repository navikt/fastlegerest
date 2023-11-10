package testhelper

import io.ktor.server.netty.*
import no.nav.syfo.application.ApplicationState
import testhelper.mock.*

class ExternalMockEnvironment {
    val applicationState: ApplicationState = testAppState()

    val azureAdMock = AzureAdMock()
    val pdlMock = PdlMock()
    val tilgangskontrollMock = VeilederTilgangskontrollMock()
    val isproxy = IsproxyMock()

    val externalApplicationMockMap = hashMapOf(
        azureAdMock.name to azureAdMock.server,
        isproxy.name to isproxy.server,
        pdlMock.name to pdlMock.server,
        tilgangskontrollMock.name to tilgangskontrollMock.server,
    )

    val environment = testEnvironment(
        azureTokenEndpoint = azureAdMock.url,
        pdlUrl = pdlMock.url,
        istilgangskontrollUrl = tilgangskontrollMock.url,
        isproxyUrl = isproxy.url,
    )

    val wellKnownInternalAzureAD = wellKnownInternalAzureAD()

    val redisServer = testRedis(environment)

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
    this.redisServer.start()
}

fun ExternalMockEnvironment.stopExternalMocks() {
    this.externalApplicationMockMap.stop()
    this.redisServer.stop()
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
