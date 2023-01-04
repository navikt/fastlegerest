package no.nav.syfo.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

const val NAV_CONSUMER_ID_HEADER = "Nav-Consumer-Id"

const val NAV_PERSONIDENT_HEADER = "nav-personident"

const val NAV_CALL_ID_HEADER = "Nav-Call-Id"

fun bearerHeader(token: String) = "Bearer $token"

fun PipelineContext<out Unit, ApplicationCall>.getCallId(): String {
    return this.call.getCallId()
}

fun ApplicationCall.getCallId(): String {
    return this.request.headers[NAV_CALL_ID_HEADER].toString()
}

fun ApplicationCall.getConsumerId(): String {
    return this.request.headers[NAV_CONSUMER_ID_HEADER].toString()
}

fun ApplicationCall.getBearerHeader(): String? {
    return getHeader(HttpHeaders.Authorization)?.removePrefix("Bearer ")
}

private fun ApplicationCall.getHeader(header: String): String? {
    return this.request.headers[header]
}

fun PipelineContext<out Unit, ApplicationCall>.getBearerHeader(): String? {
    return this.call.getBearerHeader()
}

fun PipelineContext<out Unit, ApplicationCall>.getPersonIdentHeader(): String? {
    return this.call.getHeader(NAV_PERSONIDENT_HEADER)
}
