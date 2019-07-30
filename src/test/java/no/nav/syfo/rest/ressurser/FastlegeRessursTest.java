package no.nav.syfo.rest.ressurser;

import no.nav.security.spring.oidc.test.JwtTokenGenerator;
import no.nav.syfo.LocalApplication;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nhn.schemas.reg.flr.IFlrReadOperations;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FastlegeRessursTest {

    private static final String FNR = "99999900000";
    private static final String VEILEDER_ID = "veilederID";
    private static final String LEGEKONTOR = "Pontypandy Legekontor";
    private static final String token = JwtTokenGenerator.createSignedJWT(VEILEDER_ID).serialize();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BrukerprofilV3 brukerprofilV3;

    @MockBean
    private IFlrReadOperations fastlegeSoapClient;

    @MockBean
    @Qualifier("Oidc")
    private RestTemplate restTemplate;


    @Before
    public void setUp() throws Exception {
        MockUtils.mockBrukerProfil(brukerprofilV3);
    }

    @Test
    public void finnAktivFastlege() throws Exception {
        MockUtils.mockHarFastlege(fastlegeSoapClient);
        MockUtils.mockResponseFraTilgangskontroll(restTemplate, HttpStatus.OK);

        this.mvc.perform(get("/api/fastlege/v1?fnr=" + FNR)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(LEGEKONTOR)));
    }

    @Test
    public void finnAlleFastleger() throws Exception {
        MockUtils.mockHarFastlege(fastlegeSoapClient);
        MockUtils.mockResponseFraTilgangskontroll(restTemplate, HttpStatus.OK);

        this.mvc.perform(get("/api/fastlege/v1/fastleger?fnr=" + FNR)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(LEGEKONTOR)));
    }

    @Test
    public void brukerHarIngenFastleger() throws Exception {
        MockUtils.mockIngenFastleger(fastlegeSoapClient);
        MockUtils.mockResponseFraTilgangskontroll(restTemplate, HttpStatus.OK);

        this.mvc.perform(get("/api/fastlege/v1/fastleger?fnr=" + FNR)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void brukerManglerAktivFastlege() throws Exception {
        MockUtils.mockIngenFastleger(fastlegeSoapClient);
        MockUtils.mockResponseFraTilgangskontroll(restTemplate, HttpStatus.OK);

        this.mvc.perform(get("/api/fastlege/v1?fnr=" + FNR)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mockApiError404()));
    }

    @Test
    public void brukerHarIkkeTilgang() throws Exception {
        MockUtils.mockHarFastlege(fastlegeSoapClient);
        MockUtils.mockResponseFraTilgangskontroll(restTemplate, HttpStatus.FORBIDDEN);

        this.mvc.perform(get("/api/fastlege/v1?fnr=" + FNR)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().json(mockApiError403()));
    }

    private String mockApiError403() throws JSONException {
        return mockApiErrorAsJson(403, "GEOGRAFISK");
    }

    private String mockApiError404() throws JSONException {
        return mockApiErrorAsJson(404, "Feil ved oppslag av fastlege");
    }

    private String mockApiErrorAsJson(int status, String message) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", message);
        return jsonObject.toString();
    }
}
