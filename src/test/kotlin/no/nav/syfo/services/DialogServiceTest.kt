package no.nav.syfo.services

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenResponse
import no.nav.syfo.consumer.fastlege.FastlegeConsumer
import no.nav.syfo.consumer.fastlege.PraksisInfo
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.syfopartnerinfo.PartnerInfoResponse
import no.nav.syfo.domain.*
import no.nav.syfo.domain.dialogmelding.RSHodemelding
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.client.ExpectedCount.once
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import testhelper.UserConstants.ARBEIDSTAKER_NAME_FIRST
import testhelper.UserConstants.ARBEIDSTAKER_NAME_LAST
import testhelper.UserConstants.ARBEIDSTAKER_NAME_MIDDLE
import testhelper.generatePdlHentPerson
import java.time.LocalDate
import java.util.Collections
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DialogServiceTest {
    lateinit var mockRestServiceServer: MockRestServiceServer

    @Value("\${syfopartnerinfo.url}")
    private lateinit var syfopartnerinfoUrl: String

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var azureAdV2TokenConsumer: AzureAdV2TokenConsumer

    @InjectMocks
    @Autowired
    lateinit var dialogService: DialogService

    @Inject
    @Qualifier(value = "default")
    lateinit var defaultRestTemplate: RestTemplate

    @MockBean
    lateinit var pdlConsumer: PdlConsumer

    @MockBean
    lateinit var fastlegeConsumer: FastlegeConsumer


    @Before
    fun setUp() {
        mockRestServiceServer = MockRestServiceServer
            .bindTo(defaultRestTemplate)
            .build()

        mockSyfopartnerinfo()
        Mockito.`when`(pdlConsumer.person(ArgumentMatchers.anyString()))
            .thenReturn(generatePdlHentPerson(null))
        Mockito.`when`(fastlegeConsumer.getFastleger(ArgumentMatchers.anyString()))
            .thenReturn(Collections.singletonList(generateFastlege()))
        Mockito.`when`(fastlegeConsumer.getPraksisInfo(ArgumentMatchers.anyInt()))
            .thenReturn(PraksisInfo(PARENT_HER_ID))
        mockIsdialogmelding()
    }

    @After
    fun cleanUp() {
        mockRestServiceServer.reset()
    }

    @Test
    fun sendOppfolgingsplanFraSBS() {
        val oppfolgingsplanPDF = ByteArray(20)
        val oppfolgingsplan = RSOppfolgingsplan("99999900000", oppfolgingsplanPDF)

        dialogService.sendOppfolgingsplan(oppfolgingsplan)

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
        val oppfolgingsplan = RSOppfolgingsplan("99999900000", oppfolgingsplanPDF)
        val partnerinformasjon = Partnerinformasjon(PARTNER_ID.toString(), PARENT_HER_ID.toString())
        val fastlege: Fastlege = generateFastlege()
            .pasient(
                Pasient()
                    .fornavn(ARBEIDSTAKER_NAME_FIRST)
                    .mellomnavn(ARBEIDSTAKER_NAME_MIDDLE)
                    .etternavn(ARBEIDSTAKER_NAME_LAST)
                    .fnr("99999900000")
            )

        return RSHodemelding(fastlege, partnerinformasjon, oppfolgingsplan)
    }

    private fun generateFastlege(): Fastlege {
        return Fastlege()
            .fornavn("Michaela")
            .mellomnavn("Mike")
            .etternavn("Quinn")
            .fnr("10101012345")
            .herId(HER_ID)
            .helsepersonellregisterId(123)
            .fastlegekontor(
                Fastlegekontor()
                    .navn("Pontypandy Legekontor")
                    .besoeksadresse(null)
                    .postadresse(null)
                    .telefon("")
                    .epost("")
                    .orgnummer("88888888")
            )
            .pasientforhold(
                Pasientforhold()
                    .fom(LocalDate.parse("2020-06-04"))
                    .tom(LocalDate.parse("9999-12-31"))
            )
    }

    private fun mockSyfopartnerinfo() {
        val url = "$syfopartnerinfoUrl/api/v2/behandler?herid=$PARENT_HER_ID"
        val partnerInfoResponse = PartnerInfoResponse(PARTNER_ID)
        val infoResponse: MutableList<PartnerInfoResponse> = ArrayList()
        infoResponse.add(partnerInfoResponse)

        mockRestServiceServer.expect(once(), MockRestRequestMatchers.requestTo(url))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    objectMapper.writeValueAsString(infoResponse),
                    MediaType.APPLICATION_JSON
                )
            )
    }

    companion object {
        const val HER_ID: Int = 404
        const val PARENT_HER_ID: Int = 4041
        const val PARTNER_ID: Int = 1337
    }
}
