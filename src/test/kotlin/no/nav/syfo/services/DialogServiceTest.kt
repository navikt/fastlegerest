import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.emottak.schemas.HentPartnerIDViaOrgnummerRequest
import no.nav.emottak.schemas.HentPartnerIDViaOrgnummerResponse
import no.nav.emottak.schemas.PartnerResource
import no.nav.emottak.schemas.WSPartnerInformasjon
import no.nav.syfo.LocalApplication
import no.nav.syfo.azuread.AzureAdResponse
import no.nav.syfo.domain.Token
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan
import no.nav.syfo.rest.ressurser.DialogRessurs
import no.nav.syfo.rest.ressurser.MockUtils
import no.nav.syfo.syfopartnerinfo.PartnerInfoResponse
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3
import no.nhn.register.communicationparty.ICommunicationPartyService
import no.nhn.register.communicationparty.ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage
import no.nhn.register.communicationparty.WSOrganizationPerson
import no.nhn.schemas.reg.flr.IFlrReadOperations
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)



class DialogServiceTest {

    @MockBean
    lateinit var dialogRessurs: DialogRessurs

    @MockBean
    lateinit var basicAuthRestTemplate: RestTemplate

    @MockBean
    lateinit var restTemplate: RestTemplate

    @MockBean
    lateinit var restTemplateWithProxy: RestTemplate

    @MockBean
    lateinit var adresseregisterSoapClient: ICommunicationPartyService

    @MockBean
    lateinit var brukerprofilV3: BrukerprofilV3

    @MockBean
    lateinit var fastlegeSoapClient: IFlrReadOperations

    @MockBean
    lateinit var partnerResource: PartnerResource


    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Before
    fun setUp() {
        mockPartnerResource();
        mockAdresseRegisteret();
        mockAzureAD();
        mockSyfopartnerinfo();
        MockUtils.mockBrukerProfil(brukerprofilV3);
        MockUtils.mockHarFastlege(fastlegeSoapClient);
        mockDialogfordeler();
        mockTokenService();
    }

    @Test
    fun test() { /* ... */}

    @Test
    fun sendOppfolgingsplanFraSBS() {
        val oppfolgingsplanPDF: ByteArray = ByteArray(20)
        val oppfolgingsplan: RSOppfolgingsplan = RSOppfolgingsplan("99999900000", oppfolgingsplanPDF)
    }

    private fun mockPartnerResource() {
        val anyRequest = ArgumentMatchers.any<HentPartnerIDViaOrgnummerRequest>()
        val partnerInformasjon = HentPartnerIDViaOrgnummerResponse()
                .withPartnerInformasjon(WSPartnerInformasjon()
                        .withHERid("$HER_ID")
                        .withPartnerID("$PARTNER_ID"))
        Mockito.`when`<HentPartnerIDViaOrgnummerResponse>(partnerResource.hentPartnerIDViaOrgnummer(anyRequest)).thenReturn(partnerInformasjon)
    }

    @Throws(ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage::class)
    private fun mockAdresseRegisteret() {
        val wsOrganisationPerson = WSOrganizationPerson().withParentHerId(HER_ID)
        Mockito.`when`<WSOrganizationPerson>(adresseregisterSoapClient.getOrganizationPersonDetails(ArgumentMatchers.anyInt())).thenReturn(wsOrganisationPerson)
    }

    private fun mockDialogfordeler() {
        val responseEntity200 = ResponseEntity<Any>(HttpStatus.OK)
        Mockito.`when`(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(),
                Mockito.any<Class<Any>>()
        )).thenReturn(responseEntity200)
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
        val infoResponse: MutableList<PartnerInfoResponse> = ArrayList()
        infoResponse.add(partnerInfoResponse)
        val response = ResponseEntity<List<PartnerInfoResponse>>(infoResponse, HttpStatus.OK)
        Mockito.`when`(restTemplate.exchange(
                Mockito.eq(URL),
                Mockito.eq(HttpMethod.GET),
                Mockito.any<HttpEntity<List<PartnerInfoResponse>>>(),
                Mockito.any<ParameterizedTypeReference<List<PartnerInfoResponse>>>()
        )).thenReturn(response)
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
                Mockito.anyString(),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(),
                Mockito.any<Class<Any>>()
        )).thenReturn(response)
    }


    companion object {
        const val HER_ID: Int = 1234
        const val PARTNER_ID: Int = 4321
    }
}
