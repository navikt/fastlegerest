package no.nav.syfo.rest.ressurser;

import no.nav.security.spring.oidc.test.JwtTokenGenerator;
import no.nav.syfo.LocalApplication;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nhn.schemas.reg.flr.IFlrReadOperations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FastlegeRessursTest {

    private static final String FNR = "***REMOVED***";
    private static final String VEILEDER_ID = "veilederID";
    private static final String LEGEKONTOR = "Pontypandy Legekontor";
    private static final String token = JwtTokenGenerator.createSignedJWT(VEILEDER_ID).serialize();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BrukerprofilV3 brukerprofilV3;

    @MockBean
    private IFlrReadOperations fastlegeSoapClient;

    @Before
    public void setUp() throws Exception {
        MockUtils.mockBrukerProfil(brukerprofilV3);
        MockUtils.mockFastLegeSoapClient(fastlegeSoapClient);
    }

    @Test
    public void finnFastlege() throws Exception {
        this.mvc.perform(get("/fastlege/v1?fnr=" + FNR)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(LEGEKONTOR)));
    }

    @Test
    public void finnFastleger() throws Exception {

        this.mvc.perform(get("/fastleger?fnr=" + FNR)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(LEGEKONTOR)));
    }

}