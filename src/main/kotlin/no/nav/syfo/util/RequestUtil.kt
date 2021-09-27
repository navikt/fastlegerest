package no.nav.syfo.util

import java.util.*

const val NAV_CONSUMER_TOKEN_HEADER = "Nav-Consumer-Token"

const val NAV_CONSUMER_ID_HEADER = "Nav-Consumer-Id"
const val APP_CONSUMER_ID = "syfoperson"

const val NAV_PERSONIDENT_HEADER = "nav-personident"

const val TEMA_HEADER = "Tema"
const val ALLE_TEMA_HEADERVERDI = "GEN"

const val NAV_CALL_ID_HEADER = "Nav-Call-Id"

fun createCallId(): String = UUID.randomUUID().toString()
