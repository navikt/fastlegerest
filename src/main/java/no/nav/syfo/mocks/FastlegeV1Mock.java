package no.nav.syfo.mocks;


import no.nhn.register.common.*;
import no.nhn.schemas.reg.common.en.WSPeriod;
import no.nhn.schemas.reg.flr.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@ConditionalOnProperty(value = "mockEksternHelse", havingValue = "true")
public class FastlegeV1Mock implements IFlrReadOperations {

    @Override
    public WSPatientToGPContractAssociation getPatientGPDetails(String s) {
        return new WSPatientToGPContractAssociation()
                .withGPContract(new WSGPContract()
                        .withGPOffice(new WSGPOffice()
                                .withName("MOCKDATA: Testkontoret")
                                .withOrganizationNumber(123456789)
                                .withElectronicAddresses(new WSArrayOfElectronicAddress().withElectronicAddresses(Arrays.asList(
                                        new WSElectronicAddress()
                                                .withType(new WSCode()
                                                        .withCodeValue("E_TLF")
                                                )
                                                .withAddress("MOCKDATA: 22229999"),
                                        new WSElectronicAddress()
                                                .withType(new WSCode()
                                                        .withCodeValue("E_EDI")
                                                )
                                                .withAddress("MOCKDATA: test@testkontoret.no")
                                )))
                                .withPhysicalAddresses(new WSArrayOfPhysicalAddress().withPhysicalAddresses(Arrays.asList(
                                        new WSPhysicalAddress()
                                                .withType(new WSCode()
                                                        .withCodeValue("PST")
                                                        .withActive(true)
                                                )
                                        .withPostbox("MOCKDATA: Olav Rhyes Plass")
                                        .withPostalCode(1177)
                                        .withCity("MOCKDATA: Oslo"),
                                        new WSPhysicalAddress()
                                                .withType(new WSCode()
                                                        .withCodeValue("RES")
                                                        .withActive(true)
                                                )
                                                .withStreetAddress("MOCKDATA: Sannergata 2")
                                                .withPostalCode(1177)
                                                .withCity("MOCKDATA: Oslo")
                                )))
                        )
                )
                .withPeriod(new WSPeriod().withFrom(LocalDateTime.now().minusYears(4)).withTo(LocalDateTime.now().plusYears(2)))
                .withPatient(new WSPerson()
                        .withFirstName("MOCKDATA: Sygve Sykmeldt"))
                .withDoctorCycles(new WSArrayOfGPOnContractAssociation()
                        .withGPOnContractAssociations(
                                new WSGPOnContractAssociation()
                                        .withHprNumber(12341234)
                                        .withGP(new WSPerson()
                                                .withFirstName("MOCKDATA: Lege")
                                                .withLastName("MOCKDATA: Legesen")
                                                .withNIN("MOCKDATA: 12312312312"))
                                        .withValid(new WSPeriod().withFrom(LocalDateTime.now().minusYears(4)).withTo(LocalDateTime.now().plusYears(2)))
                        ))
                ;
    }

    @Override
    public WSArrayOfGPContract getGPContractsOnOffice(Integer integer, LocalDateTime localDateTime) {
        return null;
    }

    @Override
    public WSArrayOfPatientToGPContractAssociation getPatientsGPDetailsAtTime(WSArrayOfNinWithTimestamp wsArrayOfNinWithTimestamp) {
        return null;
    }

    @Override
    public byte[] navGetEncryptedPatientList(WSNavEncryptedPatientListParameters wsNavEncryptedPatientListParameters) {
        return new byte[0];
    }

    @Override
    public WSArrayOfPatientToGPContractAssociation getGPPatientList(Long aLong) {
        return null;
    }

    @Override
    public WSGPDetails getGPWithAssociatedGPContracts(Integer integer, LocalDateTime localDateTime) {
        return null;
    }
}
