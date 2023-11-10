package no.nav.syfo.application

import no.nav.syfo.client.ClientEnvironment
import no.nav.syfo.client.ClientsEnvironment
import no.nav.syfo.client.azuread.AzureEnvironment

data class Environment(
    val namespace: String = "teamsykefravr",
    val appname: String = "fastlegerest",

    val azure: AzureEnvironment = AzureEnvironment(
        appClientId = getEnvVar("AZURE_APP_CLIENT_ID"),
        appClientSecret = getEnvVar("AZURE_APP_CLIENT_SECRET"),
        appPreAuthorizedApps = getEnvVar("AZURE_APP_PRE_AUTHORIZED_APPS"),
        appWellKnownUrl = getEnvVar("AZURE_APP_WELL_KNOWN_URL"),
        openidConfigTokenEndpoint = getEnvVar("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT"),
    ),
    val clients: ClientsEnvironment = ClientsEnvironment(
        pdl = ClientEnvironment(
            clientId = getEnvVar("PDL_CLIENT_ID"),
            baseUrl = getEnvVar("PDL_URL"),
        ),
        istilgangskontroll = ClientEnvironment(
            clientId = getEnvVar("ISTILGANGSKONTROLL_CLIENT_ID"),
            baseUrl = getEnvVar("ISTILGANGSKONTROLL_URL"),
        )
    ),
    val redisHost: String = getEnvVar("REDIS_HOST"),
    val redisPort: Int = getEnvVar("REDIS_PORT", "6379").toInt(),
    val redisSecret: String = getEnvVar("REDIS_PASSWORD"),
    val fastlegeUrl: String = getEnvVar("FASTLEGE_URL"),
    val adresseregisterUrl: String = getEnvVar("ADRESSEREGISTER_URL"),
    val nhnUsername: String = getEnvVar("NHN_USERNAME"),
    val nhnPassword: String = getEnvVar("NHN_PASSWORD"),
)

fun getEnvVar(varName: String, defaultValue: String? = null) =
    System.getenv(varName) ?: defaultValue ?: throw RuntimeException("Missing required variable \"$varName\"")
