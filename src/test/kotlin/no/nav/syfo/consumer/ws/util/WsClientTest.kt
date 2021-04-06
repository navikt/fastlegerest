package no.nav.syfo.consumer.ws.util

import no.nhn.schemas.reg.flr.IFlrReadOperations
import org.junit.Test

class WsClientTest {
    @Test
    fun testsomething() {
        val port = WsClient<IFlrReadOperations>()
            .createPort(PORT_URL, IFlrReadOperations::class.java, listOf(LogErrorHandler()))
    }

    companion object {
        const val PORT_URL = "http://url.dev"
    }
}
