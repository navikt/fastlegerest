package no.nav.syfo.fastlege.api.system

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.application.api.APISystemConsumerAccessService
import no.nav.syfo.fastlege.FastlegeService
import no.nav.syfo.fastlege.expection.FastlegeIkkeFunnet
import no.nav.syfo.fastlege.expection.FastlegevikarIkkeFunnet
import no.nav.syfo.util.*

const val FASTLEGE_SYSTEM_PATH = "/fastlegerest/api/system/v1/fastlege"

fun Route.registrerFastlegeSystemApi(
    apiConsumerAccessService: APISystemConsumerAccessService,
    fastlegeService: FastlegeService,
) {
    route(FASTLEGE_SYSTEM_PATH) {
        get("/aktiv/personident") {
            val callId = getCallId()
            val token = getBearerHeader()
                ?: throw IllegalArgumentException("No Authorization header supplied to system api when getting fastlege, callID=$callId")
            val requestedPersonIdent = getPersonIdentHeader()?.let { personIdent ->
                PersonIdent(personIdent)
            } ?: throw IllegalArgumentException("No PersonIdent supplied")

            apiConsumerAccessService.validateSystemConsumerApplicationClientId(
                authorizedApplicationNameList = listOf("isdialogmelding"),
                token = token,
            )

            val fastlege = fastlegeService.hentBrukersFastlege(requestedPersonIdent, callId)
                ?: throw FastlegeIkkeFunnet()
            call.respond(fastlege)
        }
        get("/vikar/personident") {
            val callId = getCallId()
            val token = getBearerHeader()
                ?: throw IllegalArgumentException("No Authorization header supplied to system api when getting fastlege, callID=$callId")
            val requestedPersonIdent = getPersonIdentHeader()?.let { personIdent ->
                PersonIdent(personIdent)
            } ?: throw IllegalArgumentException("No PersonIdent supplied")

            apiConsumerAccessService.validateSystemConsumerApplicationClientId(
                authorizedApplicationNameList = listOf("isdialogmelding"),
                token = token,
            )

            val vikar = fastlegeService.hentBrukersFastlegevikar(requestedPersonIdent, callId)
                ?: throw FastlegevikarIkkeFunnet()
            call.respond(vikar)
        }
    }
}
