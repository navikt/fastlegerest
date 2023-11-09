package no.nav.syfo.testhelper.mock

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.authentication.installContentNegotiation
import no.nav.syfo.client.pdl.*
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_PERSONIDENT
import no.nav.syfo.testhelper.generatePdlHentPerson
import no.nav.syfo.testhelper.generatePdlPersonNavn
import no.nav.syfo.testhelper.getRandomPort

class PdlMock {
    private val port = getRandomPort()
    val url = "http://localhost:$port"
    val name = "pdl"

    val respons = generatePdlHentPerson(generatePdlPersonNavn())

    val server = embeddedServer(
        factory = Netty,
        port = port,
    ) {
        installContentNegotiation()
        routing {
            post {
                val pdlRequest = call.receive<PdlRequest>()
                if (pdlRequest.variables.ident.equals(ARBEIDSTAKER_PERSONIDENT.value)) {
                    call.respond(respons)
                } else {
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}
