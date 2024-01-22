package no.nav.syfo.fastlege.api.system

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.APISystemConsumerAccessService
import no.nav.syfo.fastlege.FastlegeService
import no.nav.syfo.fastlege.exception.FastlegeIkkeFunnet
import no.nav.syfo.fastlege.exception.FastlegevikarIkkeFunnet
import no.nav.syfo.util.*

const val SYSTEM_PATH = "/fastlegerest/api/system/v1"
const val herIdParam = "herid"
val authorizedApplicationNameList = listOf("isdialogmelding")

fun Route.registrerFastlegeSystemApi(
    apiConsumerAccessService: APISystemConsumerAccessService,
    fastlegeService: FastlegeService,
) {
    route(SYSTEM_PATH) {
        get("/fastlege/aktiv/personident") {
            val callId = getCallId()
            val token = getBearerHeader()
                ?: throw IllegalArgumentException("No Authorization header supplied to system api when getting fastlege, callID=$callId")
            val requestedPersonIdent = getPersonIdentHeader()?.let { personIdent ->
                PersonIdent(personIdent)
            } ?: throw IllegalArgumentException("No PersonIdent supplied")

            apiConsumerAccessService.validateSystemConsumerApplicationClientId(
                authorizedApplicationNameList = authorizedApplicationNameList,
                token = token,
            )

            val fastlege = fastlegeService.hentBrukersFastlege(requestedPersonIdent)
                ?: throw FastlegeIkkeFunnet()
            call.respond(fastlege)
        }
        get("/fastlege/vikar/personident") {
            val callId = getCallId()
            val token = getBearerHeader()
                ?: throw IllegalArgumentException("No Authorization header supplied to system api when getting vikar, callID=$callId")
            val requestedPersonIdent = getPersonIdentHeader()?.let { personIdent ->
                PersonIdent(personIdent)
            } ?: throw IllegalArgumentException("No PersonIdent supplied")

            apiConsumerAccessService.validateSystemConsumerApplicationClientId(
                authorizedApplicationNameList = authorizedApplicationNameList,
                token = token,
            )

            val vikar = fastlegeService.hentBrukersFastlegevikar(requestedPersonIdent)
                ?: throw FastlegevikarIkkeFunnet()
            call.respond(vikar)
        }
        get("/{$herIdParam}/behandlere") {
            val callId = getCallId()
            val token = getBearerHeader()
                ?: throw IllegalArgumentException("No Authorization header supplied to system api when getting behandlere, callID=$callId")
            val kontorHerId = this.call.parameters[herIdParam]?.toInt()
                ?: throw IllegalArgumentException("No herId supplied")

            apiConsumerAccessService.validateSystemConsumerApplicationClientId(
                authorizedApplicationNameList = authorizedApplicationNameList,
                token = token,
            )

            val behandlerKontor = fastlegeService.hentBehandlerKontor(kontorHerId)
            if (behandlerKontor != null) {
                call.respond(behandlerKontor)
            } else {
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
