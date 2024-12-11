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
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FastlegeAzureADApiTest : Spek({
    describe(FastlegeAzureADApiTest::class.java.simpleName) {

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
            val validToken = generateJWTNavIdent(
                externalMockEnvironment.environment.azure.appClientId,
                externalMockEnvironment.wellKnownInternalAzureAD.issuer,
                VEILEDER_IDENT,
            )
            describe("Happy path") {
                it("should return fastlege") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(FASTLEGE_PATH) {
                            bearerAuth(validToken)
                            header(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.OK

                        val fastlege = response.body<Fastlege>()
                        fastlege.relasjon.kodeVerdi shouldBeEqualTo RelasjonKodeVerdi.FASTLEGE.kodeVerdi
                        fastlege.pasient!!.fnr shouldBeEqualTo FASTLEGEOPPSLAG_PERSON_ID
                    }
                }
                it("should return fastlege when both fastlege and vikar") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(FASTLEGE_PATH) {
                            bearerAuth(validToken)
                            header(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.OK

                        val fastlege = response.body<Fastlege>()
                        fastlege.relasjon.kodeVerdi shouldBeEqualTo RelasjonKodeVerdi.FASTLEGE.kodeVerdi
                        fastlege.pasient!!.fnr shouldBeEqualTo FASTLEGEOPPSLAG_PERSON_ID
                    }
                }
            }
            describe("Unhappy paths") {
                it("no fastlege") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(FASTLEGE_PATH) {
                            bearerAuth(validToken)
                            header(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT_NO_FASTLEGE.value)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.NotFound
                        response.body<String>() shouldBeEqualTo "Fant ikke aktiv fastlege"
                    }
                }
                it("veileder has no access") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(FASTLEGE_PATH) {
                            bearerAuth(validToken)
                            header(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT_VEILEDER_NO_ACCESS.value)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.Forbidden
                        response.body<String>() shouldBeEqualTo "Denied NAVIdent access to personIdent"
                    }
                }
                it("invalid fnr") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(FASTLEGE_PATH) {
                            bearerAuth(validToken)
                            header(NAV_PERSONIDENT_HEADER, "123")
                        }
                        response.status shouldBeEqualTo HttpStatusCode.BadRequest
                        response.body<String>() shouldBeEqualTo "Value is not a valid PersonIdentNumber"
                    }
                }
                it("no fnr") {
                    testApplication {
                        val client = setupApiAndClient()
                        val response = client.get(FASTLEGE_PATH) {
                            bearerAuth(validToken)
                        }
                        response.status shouldBeEqualTo HttpStatusCode.BadRequest
                        response.body<String>() shouldBeEqualTo "No PersonIdent supplied"
                    }
                }
            }
        }
    }
})
