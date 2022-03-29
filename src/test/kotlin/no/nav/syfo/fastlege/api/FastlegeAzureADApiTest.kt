package no.nav.syfo.fastlege.api

import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.fastlege.FastlegeConsumer
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.tilgangskontroll.Tilgang
import no.nav.syfo.consumer.tilgangskontroll.TilgangkontrollConsumer
import no.nav.syfo.fastlege.FastlegeService
import no.nav.syfo.fastlege.domain.Fastlege
import no.nav.syfo.fastlege.domain.RelasjonKodeVerdi
import no.nav.syfo.fastlege.expection.FastlegeIkkeFunnet
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.util.NAV_PERSONIDENT_HEADER
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FastlegeAzureADApiTest {

    @MockBean
    lateinit var fastlegeConsumer: FastlegeConsumer

    @MockBean
    lateinit var pdlConsumer: PdlConsumer

    @MockBean
    lateinit var metrikk: Metrikk

    @MockBean
    lateinit var tilgangkontrollConsumer: TilgangkontrollConsumer

    @Autowired
    lateinit var fastlegeService: FastlegeService

    @Autowired
    lateinit var fastlegeAzureADApi: FastlegeAzureADApi

    @Test
    fun `Finner ikke aktiv fastlege for PersonIdent`() {
        Mockito.`when`(tilgangkontrollConsumer.accessAzureAdV2(ARBEIDSTAKER_PERSONIDENT))
            .thenReturn(Tilgang(true))

        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT.value)

        assertThrows<FastlegeIkkeFunnet> {
            fastlegeAzureADApi.finnFastlegeAazure(headers)
        }
    }

    @Test
    fun `Hent aktiv fastlege for PersonIdent med 2 fastleger, en fastlege og en fastlegevikar`() {
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
        Mockito.`when`(tilgangkontrollConsumer.accessAzureAdV2(ARBEIDSTAKER_PERSONIDENT))
            .thenReturn(Tilgang(true))

        val result: Fastlege = fastlegeAzureADApi.finnFastlegeAazure(headers)

        assertEquals(fastlege.relasjon.kodeVerdi, result.relasjon.kodeVerdi)
        assertEquals(fastlege.stillingsprosent, result.stillingsprosent)
    }

    @Test
    fun testUgyldigFnr() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NAV_PERSONIDENT_HEADER, "123")
        assertThrows<IllegalArgumentException> {
            fastlegeAzureADApi.getFastleger(headers)
        }
    }

    @Test
    fun testNullFnr() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        assertThrows<IllegalArgumentException> {
            fastlegeAzureADApi.getFastleger(headers)
        }
    }

    @Test
    fun testFnrMedEkstraTegn() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NAV_PERSONIDENT_HEADER, "12121212345abc")
        assertThrows<IllegalArgumentException> {
            fastlegeAzureADApi.getFastleger(headers)
        }
    }

    @Test
    fun testFnrMedWhitespace() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NAV_PERSONIDENT_HEADER, "  ${ARBEIDSTAKER_PERSONIDENT.value}  ")
        assertThrows<IllegalArgumentException> {
            fastlegeAzureADApi.getFastleger(headers)
        }
    }

    @Test
    fun testFnrRiktigAntallSiffer() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NAV_PERSONIDENT_HEADER, ARBEIDSTAKER_PERSONIDENT.value)
        Mockito.`when`(tilgangkontrollConsumer.accessAzureAdV2(ARBEIDSTAKER_PERSONIDENT))
            .thenReturn(Tilgang(true))
        fastlegeAzureADApi.getFastleger(headers)
    }
}
