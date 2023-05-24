package no.nav.syfo.fastlege.api.system

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.server.testing.*
import no.nav.syfo.fastlege.domain.Fastlege
import no.nav.syfo.fastlege.domain.RelasjonKodeVerdi
import no.nav.syfo.util.*
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testhelper.*
import testhelper.UserConstants.ARBEIDSTAKER_PERSONIDENT

class FastlegeSystemApiTest : Spek({
    val objectMapper: ObjectMapper = configuredJacksonMapper()

    describe(FastlegeSystemApiTest::class.java.simpleName) {

        with(TestApplicationEngine()) {
            start()

            val externalMockEnvironment = ExternalMockEnvironment.getInstance()

            application.testApiModule(
                externalMockEnvironment = externalMockEnvironment,
            )

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
                val fastlegeSystemPath = "$FASTLEGE_SYSTEM_PATH/aktiv/personident"
                val vikarSystemPath = "$FASTLEGE_SYSTEM_PATH/vikar/personident"

                describe("Happy path") {
                    it("should return fastlege") {

                        with(
                            handleRequest(HttpMethod.Get, fastlegeSystemPath) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT.value)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastlege = objectMapper.readValue<Fastlege>(response.content!!)
                            fastlege.relasjon.kodeVerdi shouldBeEqualTo RelasjonKodeVerdi.FASTLEGE.kodeVerdi
                            fastlege.pasient!!.fnr shouldBeEqualTo ARBEIDSTAKER_PERSONIDENT.value
                        }
                    }
                    it("should return fastlege when both fastlege and vikar") {

                        with(
                            handleRequest(HttpMethod.Get, fastlegeSystemPath) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, UserConstants.ARBEIDSTAKER_PERSONIDENT_FASTLEGE_AND_VIKAR.value)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastlege = objectMapper.readValue<Fastlege>(response.content!!)
                            fastlege.relasjon.kodeVerdi shouldBeEqualTo RelasjonKodeVerdi.FASTLEGE.kodeVerdi
                            fastlege.pasient!!.fnr shouldBeEqualTo UserConstants.ARBEIDSTAKER_PERSONIDENT_FASTLEGE_AND_VIKAR.value
                        }
                    }
                    it("should return vikar") {

                        with(
                            handleRequest(HttpMethod.Get, vikarSystemPath) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, UserConstants.ARBEIDSTAKER_PERSONIDENT_FASTLEGE_AND_VIKAR.value)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastlege = objectMapper.readValue<Fastlege>(response.content!!)
                            fastlege.relasjon.kodeVerdi shouldBeEqualTo RelasjonKodeVerdi.VIKAR.kodeVerdi
                            fastlege.pasient!!.fnr shouldBeEqualTo UserConstants.ARBEIDSTAKER_PERSONIDENT_FASTLEGE_AND_VIKAR.value
                        }
                    }
                }
                describe("Unhappy paths") {
                    it("no fastlege") {
                        with(
                            handleRequest(HttpMethod.Get, fastlegeSystemPath) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, UserConstants.ARBEIDSTAKER_PERSONIDENT_NO_FASTLEGE.value)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.NotFound
                            response.content shouldBeEqualTo "Fant ikke aktiv fastlege"
                        }
                    }
                    it("token from app with no access") {
                        with(
                            handleRequest(HttpMethod.Get, fastlegeSystemPath) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(invalidToken))
                                addHeader(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT.value)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.Forbidden
                            response.content shouldBeEqualTo "Consumer with clientId=syfomodiaperson-client-id is denied access to API"
                        }
                    }
                    it("invalid fnr") {
                        with(
                            handleRequest(HttpMethod.Get, fastlegeSystemPath) {
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
                            handleRequest(HttpMethod.Get, fastlegeSystemPath) {
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
