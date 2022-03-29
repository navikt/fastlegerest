package no.nav.syfo.dialogmelding

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import no.nav.syfo.consumer.fastlege.*
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.syfopartnerinfo.PartnerInfoResponse
import no.nav.syfo.dialogmelding.api.RSOppfolgingsplan
import no.nav.syfo.dialogmelding.domain.RSHodemelding
import no.nav.syfo.dialogmelding.domain.createRSHodemelding
import no.nav.syfo.fastlege.domain.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.ExpectedCount.once
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import testhelper.UserConstants.ARBEIDSTAKER_NAME_FIRST
import testhelper.UserConstants.ARBEIDSTAKER_NAME_LAST
import testhelper.UserConstants.ARBEIDSTAKER_NAME_MIDDLE
import testhelper.UserConstants.ARBEIDSTAKER_PERSONIDENT
import testhelper.generatePdlHentPerson
import java.time.LocalDate
import javax.inject.Inject

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DialogmeldingServiceTest {
    lateinit var mockRestServiceServer: MockRestServiceServer

    @Value("\${syfopartnerinfo.url}")
    private lateinit var syfopartnerinfoUrl: String

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var azureAdV2TokenConsumer: AzureAdV2TokenConsumer

    @Autowired
    lateinit var dialogmeldingService: DialogmeldingService

    @Inject
    @Qualifier(value = "default")
    lateinit var defaultRestTemplate: RestTemplate

    @MockBean
    lateinit var pdlConsumer: PdlConsumer

    @MockBean
    lateinit var fastlegeConsumer: FastlegeConsumer

    @BeforeEach
    fun setUp() {
        mockRestServiceServer = MockRestServiceServer
            .bindTo(defaultRestTemplate)
            .build()

        mockSyfopartnerinfo()
        Mockito.`when`(pdlConsumer.person(ARBEIDSTAKER_PERSONIDENT))
            .thenReturn(generatePdlHentPerson(null))
        Mockito.`when`(fastlegeConsumer.getFastleger(ARBEIDSTAKER_PERSONIDENT))
            .thenReturn(listOf(generateFastlegeProxyDTO()))
        Mockito.`when`(fastlegeConsumer.getPraksisInfo(ArgumentMatchers.anyInt()))
            .thenReturn(PraksisInfo(PARENT_HER_ID))
        mockIsdialogmelding()
    }

    @AfterEach
    fun cleanUp() {
        mockRestServiceServer.reset()
    }

    @Test
    fun sendOppfolgingsplanFraSBS() {
        val oppfolgingsplanPDF = ByteArray(20)
        val oppfolgingsplan = RSOppfolgingsplan(
            sykmeldtFnr = ARBEIDSTAKER_PERSONIDENT.value,
            oppfolgingsplanPdf = oppfolgingsplanPDF,
        )

        dialogmeldingService.sendOppfolgingsplan(
            oppfolgingsplan = oppfolgingsplan,
        )

        mockRestServiceServer.verify()
    }

    private fun mockIsdialogmelding() {
        val rsHodemelding = mockRsHodemelding()
        val OK_RESPONSE_JSON = "{\n" + "}"
        mockRestServiceServer
            .expect(once(), MockRestRequestMatchers.requestTo("https://isdialogmeldingurl/api/v1/send/oppfolgingsplan"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.content().json(objectMapper.writeValueAsString(rsHodemelding)))
            .andRespond(MockRestResponseCreators.withSuccess(OK_RESPONSE_JSON, MediaType.APPLICATION_JSON))
    }

    private fun mockRsHodemelding(): RSHodemelding {
        val oppfolgingsplanPDF = ByteArray(20)
        val oppfolgingsplan = RSOppfolgingsplan(
            sykmeldtFnr = ARBEIDSTAKER_PERSONIDENT.value,
            oppfolgingsplanPdf = oppfolgingsplanPDF,
        )
        val partnerinformasjon = Partnerinformasjon(
            partnerId = PARTNER_ID,
            herId = PARENT_HER_ID,
        )
        val fastlege: Fastlege = generateFastlegeProxyDTO().toFastlege(
            foreldreEnhetHerId = null,
            pasient = Pasient(
                fornavn = ARBEIDSTAKER_NAME_FIRST,
                mellomnavn = ARBEIDSTAKER_NAME_MIDDLE,
                etternavn = ARBEIDSTAKER_NAME_LAST,
                fnr = ARBEIDSTAKER_PERSONIDENT.value,
            ),
        )
        return createRSHodemelding(
            fastlege = fastlege,
            partnerinformasjon = partnerinformasjon,
            oppfolgingsplan = oppfolgingsplan,
        )
    }

    private fun generateFastlegeProxyDTO() = FastlegeProxyDTO(
        fornavn = "Michaela",
        mellomnavn = "Mike",
        etternavn = "Quinn",
        fnr = "10101012345",
        herId = HER_ID,
        helsepersonellregisterId = 123,
        fastlegekontor = Fastlegekontor(
            navn = "Pontypandy Legekontor",
            besoeksadresse = null,
            postadresse = null,
            telefon = "",
            epost = "",
            orgnummer = "88888888",
        ),
        pasientforhold = Periode(
            fom = LocalDate.parse("2020-06-04"),
            tom = LocalDate.parse("9999-12-31"),
        ),
        gyldighet = Periode(
            fom = LocalDate.parse("2020-06-04"),
            tom = LocalDate.parse("9999-12-31"),
        ),
        relasjon = Relasjon(
            kodeVerdi = "LPFL",
            kodeTekst = "Fastlege",
        ),
        stillingsprosent = 100,
    )

    private fun mockSyfopartnerinfo() {
        val url = "$syfopartnerinfoUrl/api/v2/behandler?herid=$PARENT_HER_ID"
        val partnerInfoResponse = PartnerInfoResponse(PARTNER_ID)
        val infoResponse = listOf(partnerInfoResponse)

        mockRestServiceServer.expect(once(), MockRestRequestMatchers.requestTo(url))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    objectMapper.writeValueAsString(infoResponse),
                    MediaType.APPLICATION_JSON,
                )
            )
    }

    companion object {
        const val HER_ID: Int = 404
        const val PARENT_HER_ID: Int = 4041
        const val PARTNER_ID: Int = 1337
    }
}
