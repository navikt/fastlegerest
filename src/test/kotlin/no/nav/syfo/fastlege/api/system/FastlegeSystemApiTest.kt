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
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FastlegeSystemApiTest : Spek({
    describe(FastlegeSystemApiTest::class.java.simpleName) {

        val externalMockEnvironment = ExternalMockEnvironment.getInstance()

        fun ApplicationTestBuilder.setupApiAndClient(): HttpClient {
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

        describe("Finn fastlege") {
            val validToken = generateJWTSystem(
                externalMockEnvironment.environment.azure.appClientId,
                externalMockEnvironment.wellKnownInternalAzureAD.issuer,
                azp = testIsdialogmeldingClientId,
            )
            val invalidToken = generateJWTSystem(
                externalMockEnvironment.environment.azure.appClientId,
                externalMockEnvironment.wellKnownInternalAzureAD.issuer,
                azp = testSyfomodiapersonClientId,
            )
            val fastlegeSystemPath = "$SYSTEM_PATH/fastlege/aktiv/personident"
            val vikarSystemPath = "$SYSTEM_PATH/fastlege/vikar/personident"
            val behandlereSystemPath = "$SYSTEM_PATH/$PARENT_HER_ID/behandlere"
            val behandlereSystemPathInactive = "$SYSTEM_PATH/$PARENT_HER_ID_WITH_INACTIVE_BEHANDLER/behandlere"

            describe("Happy path") {
                it("should return fastlege") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(fastlegeSystemPath) {
                            bearerAuth(validToken)
                            header(NAV_PERSONIDENT_HEADER, UserConstants.FASTLEGEOPPSLAG_PERSON_ID)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.OK

                        val fastlege = response.body<Fastlege>()
                        fastlege.relasjon.kodeVerdi shouldBeEqualTo RelasjonKodeVerdi.FASTLEGE.kodeVerdi
                        fastlege.pasient!!.fnr shouldBeEqualTo UserConstants.FASTLEGEOPPSLAG_PERSON_ID
                    }
                }
                it("should return vikar") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(vikarSystemPath) {
                            bearerAuth(validToken)
                            header(NAV_PERSONIDENT_HEADER, UserConstants.FASTLEGEOPPSLAG_PERSON_ID)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.OK

                        val fastlege = response.body<Fastlege>()
                        fastlege.relasjon.kodeVerdi shouldBeEqualTo RelasjonKodeVerdi.VIKAR.kodeVerdi
                        fastlege.pasient!!.fnr shouldBeEqualTo UserConstants.FASTLEGEOPPSLAG_PERSON_ID
                    }
                }
                it("should return list of behandlere for kontor") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(behandlereSystemPath) {
                            bearerAuth(validToken)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.OK

                        val behandlerKontor = response.body<BehandlerKontor>()
                        behandlerKontor.aktiv shouldBeEqualTo true
                        behandlerKontor.behandlere.size shouldBeEqualTo 1
                        val behandler = behandlerKontor.behandlere[0]
                        behandler.aktiv shouldBeEqualTo true
                        behandler.etternavn shouldBeEqualTo UserConstants.FASTLEGE_ETTERNAVN
                        behandler.hprId shouldBeEqualTo UserConstants.FASTLEGE_HPR_NR
                        behandler.herId shouldBeEqualTo UserConstants.HER_ID
                        behandler.personIdent shouldBeEqualTo UserConstants.FASTLEGE_FNR
                    }
                }
                it("should return list of behandlere for kontor when some are inactive") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(behandlereSystemPathInactive) {
                            bearerAuth(validToken)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.OK

                        val behandlerKontor = response.body<BehandlerKontor>()
                        behandlerKontor.aktiv shouldBeEqualTo true
                        behandlerKontor.behandlere.size shouldBeEqualTo 2
                        val behandler = behandlerKontor.behandlere[0]
                        behandler.aktiv shouldBeEqualTo true
                        val behandlerInactive = behandlerKontor.behandlere[1]
                        behandlerInactive.aktiv shouldBeEqualTo false
                    }
                }
            }
            describe("Unhappy paths") {
                it("no fastlege") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(fastlegeSystemPath) {
                            bearerAuth(validToken)
                            header(NAV_PERSONIDENT_HEADER, UserConstants.ARBEIDSTAKER_PERSONIDENT_NO_FASTLEGE.value)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.NotFound
                        response.body<String>() shouldBeEqualTo "Fant ikke aktiv fastlege"
                    }
                }
                it("no fastlegevikar") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(vikarSystemPath) {
                            bearerAuth(validToken)
                            header(NAV_PERSONIDENT_HEADER, UserConstants.ARBEIDSTAKER_PERSONIDENT_NO_FASTLEGE.value)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.NotFound
                        response.body<String>() shouldBeEqualTo "Fant ikke fastlegevikar"
                    }
                }
                it("token from app with no access") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(fastlegeSystemPath) {
                            bearerAuth(invalidToken)
                            header(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT.value)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.Forbidden
                        response.body<String>() shouldBeEqualTo "Consumer with clientId=syfomodiaperson-client-id is denied access to API"
                    }
                }
                it("invalid fnr") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(fastlegeSystemPath) {
                            bearerAuth(invalidToken)
                            header(NAV_PERSONIDENT_HEADER, "123")
                        }
                        response.status shouldBeEqualTo HttpStatusCode.BadRequest
                        response.body<String>() shouldBeEqualTo "Value is not a valid PersonIdentNumber"
                    }
                }
                it("no fnr") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(fastlegeSystemPath) {
                            bearerAuth(invalidToken)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.BadRequest
                        response.body<String>() shouldBeEqualTo "No PersonIdent supplied"
                    }
                }
            }
        }
    }
})
