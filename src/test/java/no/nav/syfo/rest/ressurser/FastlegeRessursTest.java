package no.nav.syfo.rest.ressurser;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.LocalApplication;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonnavn;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserResponse;
import no.nhn.register.common.WSArrayOfElectronicAddress;
import no.nhn.register.common.WSArrayOfPhysicalAddress;
import no.nhn.schemas.reg.common.en.WSPeriod;
import no.nhn.schemas.reg.flr.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static no.nav.syfo.util.OidcTestHelper.loggInnVeileder;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
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
    public static final String LEGEKONTOR = "Pontypandy Legekontor";
    private String token;

    @Inject
    private OIDCRequestContextHolder oidcRequestContextHolder;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BrukerprofilV3 brukerprofilV3;

    @MockBean
    private IFlrReadOperations fastlegeSoapClient;

    @Before
    public void setUp() throws Exception {
        token = loggInnVeileder(oidcRequestContextHolder, VEILEDER_ID);
        mockBrukerProfil();
        mockFastLegeSoapClient();
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


    private void mockFastLegeSoapClient() throws IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage {
        WSArrayOfGPOnContractAssociation fastlege = mockFastlege();
        WSGPOffice legeKontor = mockLegeKontor();
        WSGPContract fastlegeKontrakt = new WSGPContract().withGPOffice(legeKontor);

        WSPatientToGPContractAssociation fastlegeResponse = new WSPatientToGPContractAssociation()
                .withDoctorCycles(fastlege)
                .withGPContract(fastlegeKontrakt)
                .withPeriod(new WSPeriod()
                        .withFrom(LocalDateTime.now().minusYears(4))
                        .withTo(LocalDateTime.now().plusYears(4)))
                .withGPHerId(123);

        Mockito.when(fastlegeSoapClient.getPatientGPDetails(anyString())).thenReturn(fastlegeResponse);
    }

    private WSArrayOfGPOnContractAssociation mockFastlege() {
        return new WSArrayOfGPOnContractAssociation()
                .withGPOnContractAssociations(new WSGPOnContractAssociation()
                        .withHprNumber(123)
                        .withGP(new WSPerson()
                                .withFirstName("Michaela")
                                .withMiddleName("Mike")
                                .withLastName("Quinn")
                                .withNIN("Kake"))
                );
    }

    private WSGPOffice mockLegeKontor() {
        return new WSGPOffice()
                .withName(LEGEKONTOR)
                .withOrganizationNumber(123)
                .withPhysicalAddresses(new WSArrayOfPhysicalAddress())
                .withElectronicAddresses(new WSArrayOfElectronicAddress());
    }

    private void mockBrukerProfil() throws Exception {
        WSPersonnavn wsPersonnavn = new WSPersonnavn()
                .withFornavn("Homer")
                .withMellomnavn("Jay")
                .withEtternavn("Simpson");
        WSHentKontaktinformasjonOgPreferanserResponse kontaktinfoResponse =
                new WSHentKontaktinformasjonOgPreferanserResponse()
                        .withBruker(new WSBruker()
                                .withPersonnavn(wsPersonnavn));
        WSHentKontaktinformasjonOgPreferanserRequest anyRequest = Mockito.<WSHentKontaktinformasjonOgPreferanserRequest>any();

        Mockito.when(brukerprofilV3.hentKontaktinformasjonOgPreferanser(anyRequest))
                .thenReturn(kontaktinfoResponse);
    }

}