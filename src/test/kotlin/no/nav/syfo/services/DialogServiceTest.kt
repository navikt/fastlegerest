package no.nav.syfo.services

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenResponse
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.consumer.syfopartnerinfo.PartnerInfoResponse
import no.nav.syfo.domain.*
import no.nav.syfo.domain.dialogmelding.RSHodemelding
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan
import no.nhn.register.communicationparty.*
import no.nhn.schemas.reg.flr.IFlrReadOperations
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
import testhelper.MockUtils
import testhelper.UserConstants.ARBEIDSTAKER_NAME_FIRST
import testhelper.UserConstants.ARBEIDSTAKER_NAME_LAST
import testhelper.UserConstants.ARBEIDSTAKER_NAME_MIDDLE
import testhelper.generatePdlHentPerson
import java.time.LocalDate
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DialogServiceTest {
    lateinit var mockDefaultRestServiceServer: MockRestServiceServer
    lateinit var mockRestServiceServerProxy: MockRestServiceServer

    @Value("\${syfopartnerinfo.url}")
    private lateinit var syfopartnerinfoUrl: String

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @InjectMocks
    @Autowired
    lateinit var dialogService: DialogService

    @Inject
    @Qualifier(value = "default")
    lateinit var defaultRestTemplate: RestTemplate

    @Inject
    @Qualifier(value = "restTemplateWithProxy")
    lateinit var restTemplateWithProxy: RestTemplate

    @MockBean
    lateinit var pdlConsumer: PdlConsumer

    @MockBean
    lateinit var adresseregisterSoapClient: ICommunicationPartyService

    @MockBean
    lateinit var fastlegeSoapClient: IFlrReadOperations


    @Before
    fun setUp() {
        mockDefaultRestServiceServer = MockRestServiceServer
            .bindTo(defaultRestTemplate)
            .build()

        mockRestServiceServerProxy = MockRestServiceServer
            .bindTo(restTemplateWithProxy)
            .build()

        mockAdresseRegisteret()
        mockAzureADV2()
        mockSyfopartnerinfo()
        Mockito.`when`(pdlConsumer.person(ArgumentMatchers.anyString()))
            .thenReturn(generatePdlHentPerson(null))
        MockUtils.mockHarFastlege(fastlegeSoapClient)
        mockAzureADV2()
        mockIsdialogmelding()
    }

    @After
    fun cleanUp() {
        mockDefaultRestServiceServer.reset()
        mockRestServiceServerProxy.reset()
    }

    @Test
    fun sendOppfolgingsplanFraSBS() {
        val oppfolgingsplanPDF = ByteArray(20)
        val oppfolgingsplan = RSOppfolgingsplan("99999900000", oppfolgingsplanPDF)

        dialogService.sendOppfolgingsplan(oppfolgingsplan)

        mockRestServiceServerProxy.verify()

    }

    @Throws(ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage::class)
    private fun mockAdresseRegisteret() {
        val wsOrganisationPerson = WSOrganizationPerson().withParentHerId(HER_ID)
        Mockito.`when`(adresseregisterSoapClient.getOrganizationPersonDetails(ArgumentMatchers.anyInt()))
            .thenReturn(wsOrganisationPerson)
    }

    private fun mockIsdialogmelding() {
        val rsHodemelding = mockRsHodemelding()
        val OK_RESPONSE_JSON = "{\n" + "}"
        mockRestServiceServerProxy
            .expect(once(), MockRestRequestMatchers.requestTo("https://isdialogmeldingurl/api/v1/send/oppfolgingsplan"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.content().json(objectMapper.writeValueAsString(rsHodemelding)))
            .andRespond(MockRestResponseCreators.withSuccess(OK_RESPONSE_JSON, MediaType.APPLICATION_JSON))
    }

    private fun mockRsHodemelding(): RSHodemelding {
        val oppfolgingsplanPDF = ByteArray(20)
        val oppfolgingsplan = RSOppfolgingsplan("99999900000", oppfolgingsplanPDF)
        val partnerinformasjon = Partnerinformasjon(PARTNER_ID.toString(), HER_ID.toString())
        val fastlege: Fastlege = Fastlege()
            .fornavn("Michaela")
            .mellomnavn("Mike")
            .etternavn("Quinn")
            .fnr("Kake")
            .herId(HER_ID)
            .helsepersonellregisterId("123")
            .pasient(
                Pasient()
                    .fornavn(ARBEIDSTAKER_NAME_FIRST)
                    .mellomnavn(ARBEIDSTAKER_NAME_MIDDLE)
                    .etternavn(ARBEIDSTAKER_NAME_LAST)
                    .fnr("99999900000")
            )
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
                    .fom(LocalDate.parse("2024-06-04"))
                    .tom(LocalDate.parse("2024-06-04"))
            )

        return RSHodemelding(fastlege, partnerinformasjon, oppfolgingsplan)
    }

    private fun mockSyfopartnerinfo() {
        val url = "$syfopartnerinfoUrl/api/v2/behandler?herid=$HER_ID"
        val partnerInfoResponse = PartnerInfoResponse(PARTNER_ID)
        val infoResponse: MutableList<PartnerInfoResponse> =
            ArrayList() // listOf(ResponseEntity<List<PartnerInfoResponse>>(infoResponse, HttpStatus.OK))
        infoResponse.add(partnerInfoResponse)

        mockDefaultRestServiceServer.expect(once(), MockRestRequestMatchers.requestTo(url))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    objectMapper.writeValueAsString(infoResponse),
                    MediaType.APPLICATION_JSON
                )
            )
    }

    private fun mockAzureADV2() {
        val azureAdResponse = AzureAdV2TokenResponse(
            "token",
            5000L,
        )

        mockRestServiceServerProxy
            .expect(once(), MockRestRequestMatchers.requestTo("https://login.microsoftonline.com/id/oauth2/v2.0/token"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(
                MockRestResponseCreators.withSuccess(
                    objectMapper.writeValueAsString(azureAdResponse),
                    MediaType.APPLICATION_JSON
                )
            )
    }


    companion object {
        const val HER_ID: Int = 404
        const val PARTNER_ID: Int = 1337
    }
}
