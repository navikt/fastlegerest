package no.nav.syfo.fastlege.api

import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.tilgangskontroll.Tilgang
import no.nav.syfo.consumer.tilgangskontroll.TilgangkontrollConsumer
import no.nav.syfo.fastlege.FastlegeService
import no.nav.syfo.metric.Metrikk
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

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FastlegeAzureADApiTest {

    @MockBean
    lateinit var fastlegeService: FastlegeService

    @MockBean
    lateinit var metrikk: Metrikk

    @MockBean
    lateinit var tilgangkontrollConsumer: TilgangkontrollConsumer

    @Autowired
    lateinit var fastlegeAzureADApi: FastlegeAzureADApi

    @Test
    fun testUgyldigFnr() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        assertThrows<IllegalArgumentException> {
            fastlegeAzureADApi.getFastleger(headers, "123")
        }
    }

    @Test
    fun testNullFnr() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        assertThrows<IllegalArgumentException> {
            fastlegeAzureADApi.getFastleger(headers, null)
        }
    }

    @Test
    fun testFnrMedEkstraTegn() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        assertThrows<IllegalArgumentException> {
            fastlegeAzureADApi.getFastleger(headers, "12121212345abc")
        }
    }

    @Test
    fun testFnrMedWhitespace() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        assertThrows<IllegalArgumentException> {
            fastlegeAzureADApi.getFastleger(headers, "  12121212345  ")
        }
    }

    @Test
    fun testFnrRiktigAntallSiffer() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        val fnr = "12121212345"
        Mockito.`when`(tilgangkontrollConsumer.accessAzureAdV2(fnr))
            .thenReturn(Tilgang(true))
        fastlegeAzureADApi.getFastleger(headers, fnr)
    }
}
