package no.nav.syfo.rest.ressurser;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.emottak.schemas.*;
import no.nav.security.spring.oidc.test.JwtTokenGenerator;
import no.nav.syfo.LocalApplication;
import no.nav.syfo.domain.Token;
import no.nav.syfo.domain.oppfolgingsplan.RSOppfolgingsplan;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nhn.register.communicationparty.*;
import no.nhn.schemas.reg.flr.IFlrReadOperations;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

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
        mockAdresseRegisteret();
        MockUtils.mockBrukerProfil(brukerprofilV3);
        MockUtils.mockHarFastlege(fastlegeSoapClient);
        mockPartnerResource();
        mockDialogfordeler();
        mockTokenService();
    }

    @Ignore
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
                Mockito.<HttpMethod>eq(HttpMethod.POST),
                Mockito.<HttpEntity<?>>any(),
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
}
