package no.nav.syfo.testhelper.mock

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.authentication.installContentNegotiation
import no.nav.syfo.client.tilgangskontroll.Tilgang
import no.nav.syfo.client.tilgangskontroll.VeilederTilgangskontrollClient.Companion.TILGANGSKONTROLL_PERSON_PATH
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_PERSONIDENT_VEILEDER_NO_ACCESS
import no.nav.syfo.testhelper.getRandomPort
import no.nav.syfo.util.NAV_PERSONIDENT_HEADER

class VeilederTilgangskontrollMock {
    private val port = getRandomPort()
    val url = "http://localhost:$port"
    private val tilgangFalse = Tilgang(
        erGodkjent = false,
    )
    private val tilgangTrue = Tilgang(
        erGodkjent = true,
    )

    val name = "veiledertilgangskontroll"
    val server = embeddedServer(
        factory = Netty,
        port = port,
    ) {
        installContentNegotiation()
        routing {
            get(TILGANGSKONTROLL_PERSON_PATH) {
                when {
                    call.request.headers[NAV_PERSONIDENT_HEADER] == ARBEIDSTAKER_PERSONIDENT_VEILEDER_NO_ACCESS.value -> {
                        call.respond(HttpStatusCode.Forbidden, tilgangFalse)
                    }
                    call.request.headers[NAV_PERSONIDENT_HEADER] != null -> {
                        call.respond(tilgangTrue)
                    }
                    else -> {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }
        }
    }
}
