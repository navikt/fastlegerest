package no.nav.syfo.services

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.syfo.LocalApplication
import no.nav.syfo.consumer.azuread.AzureAdResponse
import no.nav.syfo.consumer.pdl.PdlConsumer
import no.nav.syfo.domain.*
import no.nav.syfo.domain.dialogmelding.RSHodemelding
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan
import no.nav.syfo.rest.ressurser.MockUtils
import no.nav.syfo.consumer.syfopartnerinfo.PartnerInfoResponse
import no.nhn.register.communicationparty.ICommunicationPartyService
import no.nhn.register.communicationparty.ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage
import no.nhn.register.communicationparty.WSOrganizationPerson
import no.nhn.schemas.reg.flr.IFlrReadOperations
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.ExpectedCount.once
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import testhelper.UserConstants.ARBEIDSTAKER_NAME_FIRST
import testhelper.UserConstants.ARBEIDSTAKER_NAME_LAST
import testhelper.UserConstants.ARBEIDSTAKER_NAME_MIDDLE
import testhelper.generatePdlHentPerson
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DialogServiceTest {
    lateinit var mockRestServiceServer: MockRestServiceServer

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @InjectMocks
    @Autowired
    lateinit var dialogService: DialogService

    @MockBean
    @Qualifier(value = "BasicAuth")
    lateinit var basicAuthRestTemplate: RestTemplate

    @Inject
    @Qualifier(value = "Oidc")
    lateinit var restTemplate: RestTemplate

    @MockBean
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
        mockRestServiceServer = MockRestServiceServer
            .bindTo(restTemplate)
            .build()

        mockAdresseRegisteret()
        mockAzureAD()
        mockSyfopartnerinfo()
        Mockito.`when`(pdlConsumer.person(ArgumentMatchers.anyString()))
            .thenReturn(generatePdlHentPerson(null))
        MockUtils.mockHarFastlege(fastlegeSoapClient)
        mockDialogfordeler()
        mockTokenService()
    }

    @After
    fun cleanUp() {
        mockRestServiceServer.reset()
    }

    @Test
    fun sendOppfolgingsplanFraSBS() {
        val oppfolgingsplanPDF: ByteArray = ByteArray(20)
        val oppfolgingsplan: RSOppfolgingsplan = RSOppfolgingsplan("99999900000", oppfolgingsplanPDF)

        dialogService.sendOppfolgingsplan(oppfolgingsplan)

        mockRestServiceServer.verify()

    }

    @Throws(ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage::class)
    private fun mockAdresseRegisteret() {
        val wsOrganisationPerson = WSOrganizationPerson().withParentHerId(HER_ID)
        Mockito.`when`<WSOrganizationPerson>(adresseregisterSoapClient.getOrganizationPersonDetails(ArgumentMatchers.anyInt())).thenReturn(wsOrganisationPerson)
    }

    private fun mockDialogfordeler() {
        val rsHodemelding = mockRsHodemelding()
        val OK_RESPONSE_JSON = "{\n" + "}"
        mockRestServiceServer
            .expect(once(), MockRestRequestMatchers.requestTo("http://localhost:8080/api/dialogmelding/sendOppfolgingsplan"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andExpect(MockRestRequestMatchers.content().json(objectMapper.writeValueAsString(rsHodemelding)))
            .andRespond(MockRestResponseCreators.withSuccess(OK_RESPONSE_JSON, MediaType.APPLICATION_JSON))

    }

    private fun mockRsHodemelding(): RSHodemelding {
        val oppfolgingsplanPDF: ByteArray = ByteArray(20)
        val oppfolgingsplan: RSOppfolgingsplan = RSOppfolgingsplan("99999900000", oppfolgingsplanPDF)
        val partnerinformasjon = Partnerinformasjon(PARTNER_ID.toString(), HER_ID.toString())
        val fastlege: Fastlege = Fastlege()
            .fornavn("Michaela")
            .mellomnavn("Mike")
            .etternavn("Quinn")
            .fnr("Kake")
            .herId(HER_ID)
            .helsepersonellregisterId("123")
            .pasient(Pasient()
                .fornavn(ARBEIDSTAKER_NAME_FIRST)
                .mellomnavn(ARBEIDSTAKER_NAME_MIDDLE)
                .etternavn(ARBEIDSTAKER_NAME_LAST)
                .fnr("99999900000"))
            .fastlegekontor(Fastlegekontor()
                .navn("Pontypandy Legekontor")
                .besoeksadresse(null)
                .postadresse(null)
                .telefon("")
                .epost("")
                .orgnummer("88888888"))
            .pasientforhold(Pasientforhold()
                .fom(LocalDate.parse("2024-06-04"))
                .tom(LocalDate.parse("2024-06-04")))

        return RSHodemelding(fastlege, partnerinformasjon, oppfolgingsplan)
    }

    private fun mockTokenService() {
        val token = Token.builder().access_token("testtoken").build()
        Mockito.`when`(basicAuthRestTemplate.exchange(
            Mockito.anyString(),
            Mockito.eq(HttpMethod.GET),
            Mockito.any(),
            Mockito.any<Class<Any>>()
        )).thenReturn(ResponseEntity(token, HttpStatus.OK))
    }

    private fun mockSyfopartnerinfo() {
        val URL = "http://syfopartnerinfo/api/v1/behandler?herid=$HER_ID"
        val partnerInfoResponse = PartnerInfoResponse(PARTNER_ID)
        val infoResponse: MutableList<PartnerInfoResponse> = ArrayList() // listOf(ResponseEntity<List<PartnerInfoResponse>>(infoResponse, HttpStatus.OK))
        infoResponse.add(partnerInfoResponse)

        mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(URL))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
            .andRespond(MockRestResponseCreators.withSuccess(objectMapper.writeValueAsString(infoResponse), MediaType.APPLICATION_JSON))
    }

    private fun mockAzureAD() {
        val azureAdResponse = AzureAdResponse(
            "token",
            "",
            "",
            "",
            Instant.now(),
            "",
            ""
        )
        val response = ResponseEntity<Any>(azureAdResponse, HttpStatus.OK)
        Mockito.`when`(restTemplateWithProxy.exchange(
            Mockito.contains("aadaccesstoken"),
            Mockito.eq(HttpMethod.POST),
            Mockito.any(),
            Mockito.any<Class<Any>>()
        )).thenReturn(response)
    }


    companion object {
        const val HER_ID: Int = 404
        const val PARTNER_ID: Int = 1337
    }
}
