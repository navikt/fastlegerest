package no.nav.syfo.rest.ressurser;

import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSBruker;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.informasjon.WSPersonnavn;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserRequest;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.meldinger.WSHentKontaktinformasjonOgPreferanserResponse;
import no.nhn.register.fastlegeinformasjon.common.WSArrayOfElectronicAddress;
import no.nhn.register.fastlegeinformasjon.common.WSArrayOfPhysicalAddress;
import no.nhn.schemas.reg.common.en.WSPeriod;
import no.nhn.schemas.reg.flr.*;
import org.mockito.Mockito;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class MockUtils {
    public static final String LEGEKONTOR = "Pontypandy Legekontor";

    static void mockHarFastlege(IFlrReadOperations fastlegeSoapClient) throws IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage {
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

    static void mockIngenFastleger(IFlrReadOperations fastlegeSoapClient) throws IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage {
        Mockito.when(fastlegeSoapClient.getPatientGPDetails(anyString())).thenThrow(new IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage());
    }

    private static WSArrayOfGPOnContractAssociation mockFastlege() {
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

    private static WSGPOffice mockLegeKontor() {
        return new WSGPOffice()
                .withName(LEGEKONTOR)
                .withOrganizationNumber(123)
                .withPhysicalAddresses(new WSArrayOfPhysicalAddress())
                .withElectronicAddresses(new WSArrayOfElectronicAddress());
    }

    static void mockBrukerProfil(BrukerprofilV3 brukerprofilV3) throws Exception {
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

    static void mockResponseFraTilgangskontroll(RestTemplate restTemplate, HttpStatus httpStatus) {
        ResponseEntity<Object> responseEntity200 = new ResponseEntity<>(httpStatus);
        when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.<HttpMethod>eq(HttpMethod.GET),
                Mockito.<HttpEntity<?>>any(),
                Mockito.<Class<Object>>any()
        )).thenReturn(responseEntity200);
    }
}
