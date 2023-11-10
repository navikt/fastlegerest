package no.nav.syfo.client

data class ClientsEnvironment(
    val isproxy: ClientEnvironment,
    val pdl: ClientEnvironment,
    val istilgangskontroll: ClientEnvironment,
)

data class ClientEnvironment(
    val baseUrl: String,
    val clientId: String,
)
