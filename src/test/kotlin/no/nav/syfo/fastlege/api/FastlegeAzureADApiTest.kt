package no.nav.syfo.fastlege.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import no.nav.syfo.fastlege.domain.Fastlege
import no.nav.syfo.fastlege.domain.RelasjonKodeVerdi
import no.nav.syfo.testhelper.ExternalMockEnvironment
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_PERSONIDENT_NO_FASTLEGE
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_PERSONIDENT_VEILEDER_NO_ACCESS
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID
import no.nav.syfo.testhelper.UserConstants.VEILEDER_IDENT
import no.nav.syfo.testhelper.generateJWTNavIdent
import no.nav.syfo.testhelper.testApiModule
import no.nav.syfo.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FastlegeAzureADApiTest {

    private val externalMockEnvironment = ExternalMockEnvironment.getInstance()

    private fun ApplicationTestBuilder.setupApiAndClient(): HttpClient {
        application {
            testApiModule(
                externalMockEnvironment = externalMockEnvironment,
            )
        }
        val client = createClient {
            install(ContentNegotiation) {
                jackson { configure() }
            }
        }
        return client
    }

    private val validToken = generateJWTNavIdent(
        externalMockEnvironment.environment.azure.appClientId,
        externalMockEnvironment.wellKnownInternalAzureAD.issuer,
        VEILEDER_IDENT,
    )

    @Nested
    @DisplayName("Happy path")
    inner class HappyPath {
        @Test
        fun `should return fastlege`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(FASTLEGE_PATH) {
                    bearerAuth(validToken)
                    header(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID)
                }
                assertEquals(HttpStatusCode.OK, response.status)

                val fastlege = response.body<Fastlege>()
                assertEquals(RelasjonKodeVerdi.FASTLEGE.kodeVerdi, fastlege.relasjon.kodeVerdi)
                assertEquals(FASTLEGEOPPSLAG_PERSON_ID, fastlege.pasient!!.fnr)
            }
        }

        @Test
        fun `should return fastlege when both fastlege and vikar`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(FASTLEGE_PATH) {
                    bearerAuth(validToken)
                    header(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID)
                }
                assertEquals(HttpStatusCode.OK, response.status)

                val fastlege = response.body<Fastlege>()
                assertEquals(RelasjonKodeVerdi.FASTLEGE.kodeVerdi, fastlege.relasjon.kodeVerdi)
                assertEquals(FASTLEGEOPPSLAG_PERSON_ID, fastlege.pasient!!.fnr)
            }
        }
    }

    @Nested
    @DisplayName("Unhappy paths")
    inner class UnhappyPaths {
        @Test
        fun `no fastlege`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(FASTLEGE_PATH) {
                    bearerAuth(validToken)
                    header(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT_NO_FASTLEGE.value)
                }
                assertEquals(HttpStatusCode.NotFound, response.status)
                assertEquals("Fant ikke aktiv fastlege", response.body<String>())
            }
        }

        @Test
        fun `veileder has no access`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(FASTLEGE_PATH) {
                    bearerAuth(validToken)
                    header(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT_VEILEDER_NO_ACCESS.value)
                }
                assertEquals(HttpStatusCode.Forbidden, response.status)
                assertEquals("Denied NAVIdent access to personIdent", response.body<String>())
            }
        }

        @Test
        fun `invalid fnr`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(FASTLEGE_PATH) {
                    bearerAuth(validToken)
                    header(NAV_PERSONIDENT_HEADER, "123")
                }
                assertEquals(HttpStatusCode.BadRequest, response.status)
                assertEquals("Value is not a valid PersonIdentNumber", response.body<String>())
            }
        }

        @Test
        fun `no fnr`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(FASTLEGE_PATH) {
                    bearerAuth(validToken)
                }
                assertEquals(HttpStatusCode.BadRequest, response.status)
                assertEquals("No PersonIdent supplied", response.body<String>())
            }
        }
    }
}
