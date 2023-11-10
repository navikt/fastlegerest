package no.nav.syfo.fastlege.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.client.tilgangskontroll.VeilederTilgangskontrollClient
import no.nav.syfo.fastlege.FastlegeService
import no.nav.syfo.fastlege.exception.FastlegeIkkeFunnet
import no.nav.syfo.fastlege.exception.HarIkkeTilgang
import no.nav.syfo.util.*

const val FASTLEGE_PATH = "/fastlegerest/api/v2/fastlege"

fun Route.registerFastlegeAzureADApi(
    fastlegeService: FastlegeService,
    tilgangkontrollClient: VeilederTilgangskontrollClient,
) {
    route(FASTLEGE_PATH) {
        get("") {
            val callId = getCallId()
            val token = getBearerHeader()
                ?: throw IllegalArgumentException("No Authorization header supplied to system api when getting fastlege, callID=$callId")
            val requestedPersonIdent = getPersonIdentHeader()?.let { personIdent ->
                PersonIdent(personIdent)
            } ?: throw IllegalArgumentException("No PersonIdent supplied")

            if (!tilgangkontrollClient.hasAccess(callId, requestedPersonIdent, token)) {
                throw HarIkkeTilgang()
            }

            val fastlege = fastlegeService.hentBrukersFastlege(requestedPersonIdent)
                ?: throw FastlegeIkkeFunnet()
            call.respond(fastlege)
        }
        get("/fastleger") {
            val callId = getCallId()
            val token = getBearerHeader()
                ?: throw IllegalArgumentException("No Authorization header supplied to system api when getting fastlege, callID=$callId")
            val requestedPersonIdent = getPersonIdentHeader()?.let { personIdent ->
                PersonIdent(personIdent)
            } ?: throw IllegalArgumentException("No PersonIdent supplied")

            if (!tilgangkontrollClient.hasAccess(callId, requestedPersonIdent, token)) {
                throw HarIkkeTilgang()
            }

            val fastleger = fastlegeService.hentBrukersFastleger(requestedPersonIdent)
            call.respond(fastleger)
        }
    }
}
