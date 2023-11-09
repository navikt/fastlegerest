package no.nav.syfo.fastlege.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
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
    val objectMapper: ObjectMapper = configuredJacksonMapper()

    describe(FastlegeAzureADApiTest::class.java.simpleName) {

        with(TestApplicationEngine()) {
            start()

            val externalMockEnvironment = ExternalMockEnvironment.getInstance()

            application.testApiModule(
                externalMockEnvironment = externalMockEnvironment,
            )

            describe("Finn fastlege") {
                val validToken = generateJWTNavIdent(
                    externalMockEnvironment.environment.azure.appClientId,
                    externalMockEnvironment.wellKnownInternalAzureAD.issuer,
                    VEILEDER_IDENT,
                )
                describe("Happy path") {
                    it("should return fastlege") {

                        with(
                            handleRequest(HttpMethod.Get, FASTLEGE_PATH) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastlege = objectMapper.readValue<Fastlege>(response.content!!)
                            fastlege.relasjon.kodeVerdi shouldBeEqualTo RelasjonKodeVerdi.FASTLEGE.kodeVerdi
                            fastlege.pasient!!.fnr shouldBeEqualTo FASTLEGEOPPSLAG_PERSON_ID
                        }
                    }
                    it("should return fastlege when both fastlege and vikar") {

                        with(
                            handleRequest(HttpMethod.Get, FASTLEGE_PATH) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, FASTLEGEOPPSLAG_PERSON_ID)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastlege = objectMapper.readValue<Fastlege>(response.content!!)
                            fastlege.relasjon.kodeVerdi shouldBeEqualTo RelasjonKodeVerdi.FASTLEGE.kodeVerdi
                            fastlege.pasient!!.fnr shouldBeEqualTo FASTLEGEOPPSLAG_PERSON_ID
                        }
                    }
                }
                describe("Unhappy paths") {
                    it("no fastlege") {
                        with(
                            handleRequest(HttpMethod.Get, FASTLEGE_PATH) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT_NO_FASTLEGE.value)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.NotFound
                            response.content shouldBeEqualTo "Fant ikke aktiv fastlege"
                        }
                    }
                    it("veileder has no access") {
                        with(
                            handleRequest(HttpMethod.Get, FASTLEGE_PATH) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT_VEILEDER_NO_ACCESS.value)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Forbidden
                            response.content shouldBeEqualTo "Denied NAVIdent access to personIdent"
                        }
                    }
                    it("invalid fnr") {
                        with(
                            handleRequest(HttpMethod.Get, FASTLEGE_PATH) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, "123")
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.BadRequest
                            response.content shouldBeEqualTo "Value is not a valid PersonIdentNumber"
                        }
                    }
                    it("no fnr") {
                        with(
                            handleRequest(HttpMethod.Get, FASTLEGE_PATH) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.BadRequest
                            response.content shouldBeEqualTo "No PersonIdent supplied"
                        }
                    }
                }
            }
        }
    }
})
