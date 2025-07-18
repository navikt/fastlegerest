package no.nav.syfo.fastlege.api.system

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import no.nav.syfo.fastlege.domain.BehandlerKontor
import no.nav.syfo.fastlege.domain.Fastlege
import no.nav.syfo.fastlege.domain.RelasjonKodeVerdi
import no.nav.syfo.testhelper.*
import no.nav.syfo.testhelper.UserConstants.ARBEIDSTAKER_PERSONIDENT
import no.nav.syfo.testhelper.UserConstants.PARENT_HER_ID
import no.nav.syfo.testhelper.UserConstants.PARENT_HER_ID_WITH_INACTIVE_BEHANDLER
import no.nav.syfo.util.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FastlegeSystemApiTest {

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

    private val validToken = generateJWTSystem(
        externalMockEnvironment.environment.azure.appClientId,
        externalMockEnvironment.wellKnownInternalAzureAD.issuer,
        azp = testIsdialogmeldingClientId,
    )
    private val invalidToken = generateJWTSystem(
        externalMockEnvironment.environment.azure.appClientId,
        externalMockEnvironment.wellKnownInternalAzureAD.issuer,
        azp = testSyfomodiapersonClientId,
    )
    private val fastlegeSystemPath = "$SYSTEM_PATH/fastlege/aktiv/personident"
    private val vikarSystemPath = "$SYSTEM_PATH/fastlege/vikar/personident"
    private val behandlereSystemPath = "$SYSTEM_PATH/$PARENT_HER_ID/behandlere"
    private val behandlereSystemPathInactive = "$SYSTEM_PATH/$PARENT_HER_ID_WITH_INACTIVE_BEHANDLER/behandlere"

    @Nested
    @DisplayName("Happy path")
    inner class HappyPath {
        @Test
        fun `should return fastlege`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(fastlegeSystemPath) {
                    bearerAuth(validToken)
                    header(NAV_PERSONIDENT_HEADER, UserConstants.FASTLEGEOPPSLAG_PERSON_ID)
                }
                assertEquals(HttpStatusCode.OK, response.status)

                val fastlege = response.body<Fastlege>()
                assertEquals(RelasjonKodeVerdi.FASTLEGE.kodeVerdi, fastlege.relasjon.kodeVerdi)
                assertEquals(UserConstants.FASTLEGEOPPSLAG_PERSON_ID, fastlege.pasient!!.fnr)
            }
        }

        @Test
        fun `should return vikar`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(vikarSystemPath) {
                    bearerAuth(validToken)
                    header(NAV_PERSONIDENT_HEADER, UserConstants.FASTLEGEOPPSLAG_PERSON_ID)
                }
                assertEquals(HttpStatusCode.OK, response.status)

                val fastlege = response.body<Fastlege>()
                assertEquals(RelasjonKodeVerdi.VIKAR.kodeVerdi, fastlege.relasjon.kodeVerdi)
                assertEquals(UserConstants.FASTLEGEOPPSLAG_PERSON_ID, fastlege.pasient!!.fnr)
            }
        }

        @Test
        fun `should return list of behandlere for kontor`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(behandlereSystemPath) {
                    bearerAuth(validToken)
                }
                assertEquals(HttpStatusCode.OK, response.status)

                val behandlerKontor = response.body<BehandlerKontor>()
                assertTrue(behandlerKontor.aktiv)
                assertEquals(1, behandlerKontor.behandlere.size)
                val behandler = behandlerKontor.behandlere[0]
                assertTrue(behandler.aktiv)
                assertEquals(UserConstants.FASTLEGE_ETTERNAVN, behandler.etternavn)
                assertEquals(UserConstants.FASTLEGE_HPR_NR, behandler.hprId)
                assertEquals(UserConstants.HER_ID, behandler.herId)
                assertEquals(UserConstants.FASTLEGE_FNR, behandler.personIdent)
            }
        }

        @Test
        fun `should return list of behandlere for kontor when some are inactive`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(behandlereSystemPathInactive) {
                    bearerAuth(validToken)
                }
                assertEquals(HttpStatusCode.OK, response.status)

                val behandlerKontor = response.body<BehandlerKontor>()
                assertTrue(behandlerKontor.aktiv)
                assertEquals(2, behandlerKontor.behandlere.size)
                val behandler = behandlerKontor.behandlere[0]
                assertTrue(behandler.aktiv)
                val behandlerInactive = behandlerKontor.behandlere[1]
                assertFalse(behandlerInactive.aktiv)
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
                val response = client.get(fastlegeSystemPath) {
                    bearerAuth(validToken)
                    header(NAV_PERSONIDENT_HEADER, UserConstants.ARBEIDSTAKER_PERSONIDENT_NO_FASTLEGE.value)
                }
                assertEquals(HttpStatusCode.NotFound, response.status)
                assertEquals("Fant ikke aktiv fastlege", response.body<String>())
            }
        }

        @Test
        fun `no fastlegevikar`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(vikarSystemPath) {
                    bearerAuth(validToken)
                    header(NAV_PERSONIDENT_HEADER, UserConstants.ARBEIDSTAKER_PERSONIDENT_NO_FASTLEGE.value)
                }
                assertEquals(HttpStatusCode.NotFound, response.status)
                assertEquals("Fant ikke fastlegevikar", response.body<String>())
            }
        }

        @Test
        fun `token from app with no access`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(fastlegeSystemPath) {
                    bearerAuth(invalidToken)
                    header(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT.value)
                }
                assertEquals(HttpStatusCode.Forbidden, response.status)
                assertEquals(
                    "Consumer with clientId=syfomodiaperson-client-id is denied access to API",
                    response.body<String>()
                )
            }
        }

        @Test
        fun `invalid fnr`() {
            testApplication {
                val client = setupApiAndClient()
                val response = client.get(fastlegeSystemPath) {
                    bearerAuth(invalidToken)
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
                val response = client.get(fastlegeSystemPath) {
                    bearerAuth(invalidToken)
                }
                assertEquals(HttpStatusCode.BadRequest, response.status)
                assertEquals("No PersonIdent supplied", response.body<String>())
            }
        }
    }
}
