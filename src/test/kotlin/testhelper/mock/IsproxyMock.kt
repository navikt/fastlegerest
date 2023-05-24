package testhelper.mock

import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.client.fastlege.FastlegeProxyDTO
import no.nav.syfo.fastlege.domain.*
import no.nav.syfo.util.*
import testhelper.UserConstants
import testhelper.getRandomPort
import java.time.LocalDate

fun generateFastlegeResponse(
    relasjonKodeVerdi: RelasjonKodeVerdi = RelasjonKodeVerdi.FASTLEGE
) = FastlegeProxyDTO(
    fornavn = "",
    mellomnavn = "",
    etternavn = "",
    fastlegekontor = Fastlegekontor(
        navn = "kontoret",
        orgnummer = null,
        besoeksadresse = null,
        postadresse = null,
        telefon = "99999999",
        epost = "kontoret@kontor.no"
    ),
    fnr = null,
    herId = null,
    gyldighet = Periode(LocalDate.now().minusYears(1), LocalDate.now().plusYears(1)),
    helsepersonellregisterId = null,
    pasientforhold = Periode(LocalDate.now().minusYears(1), LocalDate.now().plusYears(1)),
    relasjon = Relasjon(relasjonKodeVerdi.kodeVerdi, ""),
    stillingsprosent = null,
)

class IsproxyMock {
    private val port = getRandomPort()
    private val fastlegePath = "/api/v1/fastlege"
    val url = "http://localhost:$port"

    val name = "isproxy"
    val server = mockIsproxyServer(
        port
    )

    private fun mockIsproxyServer(
        port: Int
    ): NettyApplicationEngine {
        return embeddedServer(
            factory = Netty,
            port = port
        ) {
            install(ContentNegotiation) {
                jackson { configure() }
            }
            routing {
                get(fastlegePath) {
                    val personIdent = call.request.headers[NAV_PERSONIDENT_HEADER]
                    if (personIdent.equals(UserConstants.ARBEIDSTAKER_PERSONIDENT.value)) {
                        call.respond(listOf(generateFastlegeResponse()))
                    } else if (personIdent.equals(UserConstants.ARBEIDSTAKER_PERSONIDENT_FASTLEGE_AND_VIKAR.value)) {
                        call.respond(
                            listOf(
                                generateFastlegeResponse(),
                                generateFastlegeResponse(RelasjonKodeVerdi.VIKAR),
                            )
                        )
                    } else {
                        call.respond(emptyList<FastlegeProxyDTO>())
                    }
                }
            }
        }
    }
}
