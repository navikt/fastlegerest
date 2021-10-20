package no.nav.syfo.rest.ressurser

import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.fastlege.PraksisInfo
import no.nav.syfo.consumer.tilgangskontroll.Tilgang
import no.nav.syfo.consumer.tilgangskontroll.TilgangkontrollConsumer
import no.nav.syfo.metric.Metrikk
import no.nav.syfo.services.DialogServiceTest
import no.nav.syfo.services.FastlegeService
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.lang.IllegalArgumentException

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class FastlegeAzureAD2RessursTest {

    @MockBean
    lateinit var fastlegeService: FastlegeService

    @MockBean
    lateinit var metrikk: Metrikk

    @MockBean
    lateinit var tilgangkontrollConsumer: TilgangkontrollConsumer

    @InjectMocks
    @Autowired
    lateinit var fastlegeAzureAD2Ressurs: FastlegeAzureAD2Ressurs

    @Test
    fun testUgyldigFnr() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        assertThrows<IllegalArgumentException> {
            fastlegeAzureAD2Ressurs.getFastleger(headers, "123")
        }
    }

    @Test
    fun testNullFnr() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        assertThrows<IllegalArgumentException> {
            fastlegeAzureAD2Ressurs.getFastleger(headers, null)
        }
    }

    @Test
    fun testFnrMedEkstraTegn() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        assertThrows<IllegalArgumentException> {
            fastlegeAzureAD2Ressurs.getFastleger(headers, "12121212345abc")
        }
    }

    @Test
    fun testFnrMedWhitespace() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        assertThrows<IllegalArgumentException> {
            fastlegeAzureAD2Ressurs.getFastleger(headers, "  12121212345  ")
        }
    }

    @Test
    fun testFnrRiktigAntallSiffer() {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        val fnr = "12121212345"
        Mockito.`when`(tilgangkontrollConsumer.accessAzureAdV2(fnr))
            .thenReturn(Tilgang(true))
        fastlegeAzureAD2Ressurs.getFastleger(headers, fnr)
    }
}
