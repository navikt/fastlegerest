package no.nav.syfo.rest.ressurser;

import no.nav.security.spring.oidc.test.JwtTokenGenerator;
import no.nav.syfo.LocalApplication;
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

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//Disse testene må ha local_mock i application.yaml satt til False for å kjøre
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TilgangRessursTest {

    private static final String VEILEDER_ID = "veilederID";
    private static final String token = JwtTokenGenerator.createSignedJWT(VEILEDER_ID).serialize();

    @MockBean
    @Qualifier("Oidc")
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mvc;


    @Test
    public void godkjennRiktigTilgang() throws Exception {
        MockUtils.mockResponseFraTilgangskontroll(restTemplate, HttpStatus.OK);
        this.mvc.perform(get("/api/tilgang")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("true")));
    }

    @Test
    public void avslaFeilTilgang() throws Exception {
        MockUtils.mockResponseFraTilgangskontroll(restTemplate, HttpStatus.FORBIDDEN);
        this.mvc.perform(get("/api/tilgang")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("false")));
    }

    @Test
    public void svarMed401NarTokenMangler() throws Exception{
        this.mvc.perform(get("/api/tilgang")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer "))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Autorisasjonsfeil")));

    }

    @Test
    public void svarMed401NarAuthorizationHeaderMangler() throws Exception{
        this.mvc.perform(get("/api/tilgang")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Autorisasjonsfeil")));

    }

}