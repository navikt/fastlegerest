package no.nav.syfo.fastlege.api.system

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.fastlege.FastlegeConsumer
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.fastlege.domain.Fastlege
import no.nav.syfo.fastlege.domain.RelasjonKodeVerdi
import no.nav.syfo.fastlege.expection.FastlegeIkkeFunnet
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.util.NAV_PERSONIDENT_HEADER
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import testhelper.UserConstants.ARBEIDSTAKER_PERSONIDENT
import testhelper.generatePdlHentPerson
import testhelper.generator.generateFastlegeProxyDTO
import testhelper.logInSystemConsumerClient

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FastlegeSystemApiTest {

    @MockBean
    lateinit var fastlegeConsumer: FastlegeConsumer

    @MockBean
    lateinit var pdlConsumer: PdlConsumer

    @MockBean
    lateinit var metrikk: Metrikk

    @Autowired
    private lateinit var oidcRequestContextHolder: TokenValidationContextHolder

    @Autowired
    lateinit var fastlegeSystemApi: FastlegeSystemApi

    @AfterEach
    fun tearDown() {
        oidcRequestContextHolder.tokenValidationContext = null
    }

    @Test
    fun `Hent aktiv Fastlege for PersonIdent med 2 Fastleger, 1 Fastlege og 1 Fastlegevikar`() {
        logInSystemConsumerClient(oidcRequestContextHolder, "isdialogmelding-client-id")

        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT.value)

        val fastlege = generateFastlegeProxyDTO(
            relasjonKodeVerdi = RelasjonKodeVerdi.FASTLEGE,
        )
        val fastlegeVikar = generateFastlegeProxyDTO(
            relasjonKodeVerdi = RelasjonKodeVerdi.VIKAR,
        )
        val fastlegeProxyDTOList = listOf(
            fastlegeVikar,
            fastlege,
        )
        val pdlPerson = generatePdlHentPerson(null)

        Mockito.`when`(fastlegeConsumer.getFastleger(ARBEIDSTAKER_PERSONIDENT))
            .thenReturn(fastlegeProxyDTOList)
        Mockito.`when`(pdlConsumer.person(ARBEIDSTAKER_PERSONIDENT))
            .thenReturn(pdlPerson)

        val result: Fastlege = fastlegeSystemApi.fastlege(headers)

        assertEquals(fastlege.relasjon.kodeVerdi, result.relasjon.kodeVerdi)
    }

    @Test
    fun `Finner ikke aktiv fastlege for PersonIdent`() {
        logInSystemConsumerClient(oidcRequestContextHolder, "isdialogmelding-client-id")

        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT.value)

        assertThrows<FastlegeIkkeFunnet> {
            fastlegeSystemApi.fastlege(headers)
        }
    }

    @Test
    fun `Test ugyldig PersonIdent`() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NAV_PERSONIDENT_HEADER, "123")
        assertThrows<IllegalArgumentException> {
            fastlegeSystemApi.fastlege(headers)
        }
    }

    @Test
    fun `Test PersonIdent som er null`() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        assertThrows<IllegalArgumentException> {
            fastlegeSystemApi.fastlege(headers)
        }
    }

    @Test
    fun `Test PersonIdent med ekstra tegn`() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NAV_PERSONIDENT_HEADER, "12121212345abc")
        assertThrows<IllegalArgumentException> {
            fastlegeSystemApi.fastlege(headers)
        }
    }

    @Test
    fun `Test PersonIdent med whitespace`() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NAV_PERSONIDENT_HEADER, "  ${ARBEIDSTAKER_PERSONIDENT.value}  ")
        assertThrows<IllegalArgumentException> {
            fastlegeSystemApi.fastlege(headers)
        }
    }
}
