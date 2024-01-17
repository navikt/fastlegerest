package no.nav.syfo.fastlege.api.system

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.*
import io.ktor.server.testing.*
import no.nav.syfo.fastlege.domain.Behandler
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
                val fastlegeSystemPath = "$SYSTEM_PATH/fastlege/aktiv/personident"
                val vikarSystemPath = "$SYSTEM_PATH/fastlege/vikar/personident"
                val behandlereSystemPath = "$SYSTEM_PATH/$PARENT_HER_ID/behandlere"
                val behandlereSystemPathInactive = "$SYSTEM_PATH/$PARENT_HER_ID_WITH_INACTIVE_BEHANDLER/behandlere"

                describe("Happy path") {
                    it("should return fastlege") {

                        with(
                            handleRequest(HttpMethod.Get, fastlegeSystemPath) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, UserConstants.FASTLEGEOPPSLAG_PERSON_ID)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastlege = objectMapper.readValue<Fastlege>(response.content!!)
                            fastlege.relasjon.kodeVerdi shouldBeEqualTo RelasjonKodeVerdi.FASTLEGE.kodeVerdi
                            fastlege.pasient!!.fnr shouldBeEqualTo UserConstants.FASTLEGEOPPSLAG_PERSON_ID
                        }
                    }
                    it("should return fastlege when both fastlege and vikar") {

                        with(
                            handleRequest(HttpMethod.Get, fastlegeSystemPath) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, UserConstants.FASTLEGEOPPSLAG_PERSON_ID)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastlege = objectMapper.readValue<Fastlege>(response.content!!)
                            fastlege.relasjon.kodeVerdi shouldBeEqualTo RelasjonKodeVerdi.FASTLEGE.kodeVerdi
                            fastlege.pasient!!.fnr shouldBeEqualTo UserConstants.FASTLEGEOPPSLAG_PERSON_ID
                        }
                    }
                    it("should return vikar") {

                        with(
                            handleRequest(HttpMethod.Get, vikarSystemPath) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, UserConstants.FASTLEGEOPPSLAG_PERSON_ID)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val fastlege = objectMapper.readValue<Fastlege>(response.content!!)
                            fastlege.relasjon.kodeVerdi shouldBeEqualTo RelasjonKodeVerdi.VIKAR.kodeVerdi
                            fastlege.pasient!!.fnr shouldBeEqualTo UserConstants.FASTLEGEOPPSLAG_PERSON_ID
                        }
                    }
                    it("should return list of behandlere for kontor") {

                        with(
                            handleRequest(HttpMethod.Get, behandlereSystemPath) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val behandlere = objectMapper.readValue<List<Behandler>>(response.content!!)
                            behandlere.size shouldBeEqualTo 1
                            val behandler = behandlere[0]
                            behandler.aktiv shouldBeEqualTo true
                            behandler.etternavn shouldBeEqualTo UserConstants.FASTLEGE_ETTERNAVN
                            behandler.hprId shouldBeEqualTo UserConstants.FASTLEGE_HPR_NR.toString()
                            behandler.herId shouldBeEqualTo UserConstants.HER_ID
                            behandler.personIdent shouldBeEqualTo UserConstants.FASTLEGE_FNR
                        }
                    }
                    it("should return list of behandlere for kontor when some are inactive") {

                        with(
                            handleRequest(HttpMethod.Get, behandlereSystemPathInactive) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK
                            val behandlere = objectMapper.readValue<List<Behandler>>(response.content!!)
                            behandlere.size shouldBeEqualTo 2
                            val behandler = behandlere[0]
                            behandler.aktiv shouldBeEqualTo true
                            val behandlerInactive = behandlere[1]
                            behandlerInactive.aktiv shouldBeEqualTo false
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
                    it("no fastlegevikar") {
                        with(
                            handleRequest(HttpMethod.Get, vikarSystemPath) {
                                addHeader(HttpHeaders.Authorization, bearerHeader(validToken))
                                addHeader(NAV_PERSONIDENT_HEADER, UserConstants.ARBEIDSTAKER_PERSONIDENT_NO_FASTLEGE.value)
                            }
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.NotFound
                            response.content shouldBeEqualTo "Fant ikke fastlegevikar"
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
