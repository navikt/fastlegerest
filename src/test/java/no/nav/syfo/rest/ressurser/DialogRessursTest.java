package no.nav.syfo.rest.ressurser;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.emottak.schemas.HentPartnerIDViaOrgnummerRequest;
import no.nav.emottak.schemas.HentPartnerIDViaOrgnummerResponse;
import no.nav.emottak.schemas.PartnerResource;
import no.nav.emottak.schemas.WSPartnerInformasjon;
import no.nav.security.spring.oidc.test.JwtTokenGenerator;
import no.nav.syfo.LocalApplication;
import no.nav.syfo.azuread.AzureAdResponse;
import no.nav.syfo.domain.Token;
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan;
import no.nav.syfo.syfopartnerinfo.PartnerInfoResponse;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nhn.register.communicationparty.ICommunicationPartyService;
import no.nhn.register.communicationparty.ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage;
import no.nhn.register.communicationparty.WSOrganizationPerson;
import no.nhn.schemas.reg.flr.IFlrReadOperations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DialogRessursTest {

    private static final int HER_ID = 123;
    private static final String VEILEDER_ID = "veilederID";
    private static final String VEILEDER_TOKEN = JwtTokenGenerator.createSignedJWT(VEILEDER_ID).serialize();

    @MockBean
    private ICommunicationPartyService adresseregisterSoapClient;

    @MockBean
    private BrukerprofilV3 brukerprofilV3;

    @MockBean
    private IFlrReadOperations fastlegeSoapClient;

    @MockBean
    private PartnerResource partnerResource;

    @MockBean
    @Qualifier("restTemplateWithProxy")
    private RestTemplate restTemplateWithProxy;


    @MockBean
    @Qualifier("BasicAuth")
    private RestTemplate basicAuthRestTemplate;

    @MockBean
    @Qualifier("Oidc")
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
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
    public void sendOppfolgingsplan() throws Exception {
        byte[] oppfolgingsplanPDF = new byte[20];
        RSOppfolgingsplan oppfolgingsplan = new RSOppfolgingsplan("99999900000", oppfolgingsplanPDF);

        this.mvc.perform(post("/api/dialogmelding/v1/sendOppfolgingsplanFraSelvbetjening")
                .header("Authorization", "Bearer " + VEILEDER_TOKEN)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oppfolgingsplan))
        ).andExpect(status().isOk());
    }

    private void mockPartnerResource() {
        HentPartnerIDViaOrgnummerRequest anyRequest = any();
        HentPartnerIDViaOrgnummerResponse partnerInformasjon = new HentPartnerIDViaOrgnummerResponse()
                .withPartnerInformasjon(new WSPartnerInformasjon()
                        .withHERid("123")
                        .withPartnerID("123"));
        when(partnerResource.hentPartnerIDViaOrgnummer(anyRequest)).thenReturn(partnerInformasjon);
    }

    private void mockAdresseRegisteret() throws ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage {
        WSOrganizationPerson wsOrganisationPerson = new WSOrganizationPerson().withParentHerId(HER_ID);
        when(adresseregisterSoapClient.getOrganizationPersonDetails(anyInt())).thenReturn(wsOrganisationPerson);
    }

    private void mockDialogfordeler() {
        ResponseEntity<Object> responseEntity200 = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(),
                Mockito.<Class<Object>>any()
        )).thenReturn(responseEntity200);
    }

    private void mockTokenService() {
        Token token = Token.builder().access_token("testtoken").build();
        when(basicAuthRestTemplate.exchange(
                Mockito.anyString(),
                Mockito.<HttpMethod>eq(HttpMethod.GET),
                Mockito.<HttpEntity<?>>any(),
                Mockito.<Class<Object>>any()
        )).thenReturn(new ResponseEntity<>(token, HttpStatus.OK));
    }

    private void mockSyfopartnerinfo() {
        final String URL = "http://syfopartnerinfo/api/v1/behandler?herid=123";
        PartnerInfoResponse partnerInfoResponse = new PartnerInfoResponse(123);
        List<PartnerInfoResponse> infoResponse = new ArrayList<>();
        infoResponse.add(partnerInfoResponse);
        ResponseEntity<List<PartnerInfoResponse>> response = new ResponseEntity<>(infoResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                Mockito.eq(URL),
                Mockito.eq(HttpMethod.GET),
                Mockito.<HttpEntity<List<PartnerInfoResponse>>>any(),
                Mockito.<ParameterizedTypeReference<List<PartnerInfoResponse>>>any()
        )).thenReturn(response);
    }

    private void mockAzureAD() {
        AzureAdResponse azureAdResponse = new AzureAdResponse(
                "token",
                "",
                "",
                "",
                Instant.now(),
                "",
                ""
        );
        ResponseEntity<Object> response = new ResponseEntity<>(azureAdResponse, HttpStatus.OK);

        when(restTemplateWithProxy.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(),
                Mockito.<Class<Object>>any()
        )).thenReturn(response);
    }

}
