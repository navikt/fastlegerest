package no.nav.syfo.rest.ressurser;

import no.nhn.register.fastlegeinformasjon.common.WSArrayOfElectronicAddress;
import no.nhn.register.fastlegeinformasjon.common.WSArrayOfPhysicalAddress;
import no.nhn.schemas.reg.common.en.WSPeriod;
import no.nhn.schemas.reg.flr.*;
import org.mockito.Mockito;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.xml.datatype.*;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class MockUtils {
    public static final String LEGEKONTOR = "Pontypandy Legekontor";


    public static XMLGregorianCalendar toXMLGregorianCalendar(LocalDateTime localDateTime) {
        XMLGregorianCalendar xmlGregorianCalendar =
                null;
        try {
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(localDateTime.toString());
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        return xmlGregorianCalendar;
    }

    public static void mockHarFastlege(IFlrReadOperations fastlegeSoapClient) throws IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage {
        WSArrayOfGPOnContractAssociation fastlege = mockFastlege();
        WSGPOffice legeKontor = mockLegeKontor();
        WSGPContract fastlegeKontrakt = new WSGPContract().withGPOffice(legeKontor);

        WSPatientToGPContractAssociation fastlegeResponse = new WSPatientToGPContractAssociation()
                .withDoctorCycles(fastlege)
                .withGPContract(fastlegeKontrakt)
                .withPeriod(new WSPeriod()
                        .withFrom(toXMLGregorianCalendar(LocalDateTime.now().minusYears(4)))
                        .withTo(toXMLGregorianCalendar(LocalDateTime.now().plusYears(4))))
                .withGPHerId(404);

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
                .withOrganizationNumber(88888888)
                .withPhysicalAddresses(new WSArrayOfPhysicalAddress())
                .withElectronicAddresses(new WSArrayOfElectronicAddress());
    }

    static void mockResponseFraTilgangskontroll(RestTemplate restTemplate, HttpStatus httpStatus) {
        String okJson = "{\"harTilgang\":true, \"begrunnelse\":\"\"}";
        String forbiddenJson = "{\"harTilgang\":false, \"begrunnelse\":\"GEOGRAFISK\"}";

        ResponseEntity<Object> responseEntity = httpStatus == HttpStatus.FORBIDDEN
                ? new ResponseEntity<>(forbiddenJson, httpStatus)
                : new ResponseEntity<>(okJson, httpStatus);

        when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.<HttpMethod>eq(HttpMethod.GET),
                Mockito.<HttpEntity<?>>any(),
                Mockito.<Class<Object>>any()
        )).thenReturn(responseEntity);
    }
}
