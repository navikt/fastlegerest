package no.nav.syfo.mocks;


import no.nhn.register.common.*;
import no.nhn.schemas.reg.common.en.WSPeriod;
import no.nhn.schemas.reg.flr.*;

import java.time.LocalDateTime;
import java.util.Arrays;

public class FastlegeV1Mock implements IFlrReadOperations {

    @Override
    public WSPatientToGPContractAssociation getPatientGPDetails(String s) throws IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage {
        return new WSPatientToGPContractAssociation()
                .withGPContract(new WSGPContract()
                        .withGPOffice(new WSGPOffice()
                                .withName("Testkontoret")
                                .withOrganizationNumber(123456789)
                                .withElectronicAddresses(new WSArrayOfElectronicAddress().withElectronicAddresses(Arrays.asList(
                                        new WSElectronicAddress()
                                                .withType(new WSCode()
                                                        .withCodeValue("E_TLF")
                                                )
                                                .withAddress("22229999"),
                                        new WSElectronicAddress()
                                                .withType(new WSCode()
                                                        .withCodeValue("E_EDI")
                                                )
                                                .withAddress("test@testkontoret.no")
                                )))
                                .withPhysicalAddresses(new WSArrayOfPhysicalAddress().withPhysicalAddresses(Arrays.asList(
                                        new WSPhysicalAddress()
                                                .withType(new WSCode()
                                                        .withCodeValue("PST")
                                                        .withActive(true)
                                                )
                                        .withPostbox("Olav Rhyes Plass")
                                        .withPostalCode(1177)
                                        .withCity("Oslo"),
                                        new WSPhysicalAddress()
                                                .withType(new WSCode()
                                                        .withCodeValue("RES")
                                                        .withActive(true)
                                                )
                                                .withStreetAddress("Sannergata 2")
                                                .withPostalCode(1177)
                                                .withCity("Oslo")
                                )))
                        )
                )
                .withPeriod(new WSPeriod().withFrom(LocalDateTime.now().minusYears(4)).withTo(LocalDateTime.now().plusYears(2)))
                .withPatient(new WSPerson()
                        .withFirstName("Sygve Sykmeldt"))
                .withDoctorCycles(new WSArrayOfGPOnContractAssociation()
                        .withGPOnContractAssociations(
                                new WSGPOnContractAssociation()
                                        .withGP(new WSPerson().withFirstName("Lege").withLastName("Legesen"))
                                        .withValid(new WSPeriod().withFrom(LocalDateTime.now().minusYears(4)).withTo(LocalDateTime.now().plusYears(2)))
                        ))
                ;
    }

    @Override
    public WSArrayOfGPContract getGPContractsOnOffice(Integer integer, LocalDateTime localDateTime) throws IFlrReadOperationsGetGPContractsOnOfficeGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSArrayOfPatientToGPContractAssociation getPatientsGPDetailsAtTime(WSArrayOfNinWithTimestamp wsArrayOfNinWithTimestamp) throws IFlrReadOperationsGetPatientsGPDetailsAtTimeGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public byte[] navGetEncryptedPatientList(WSNavEncryptedPatientListParameters wsNavEncryptedPatientListParameters) throws IFlrReadOperationsNavGetEncryptedPatientListGenericFaultFaultFaultMessage {
        return new byte[0];
    }

    @Override
    public WSArrayOfPatientToGPContractAssociation getGPPatientList(Long aLong) throws IFlrReadOperationsGetGPPatientListGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSGPDetails getGPWithAssociatedGPContracts(Integer integer, LocalDateTime localDateTime) throws IFlrReadOperationsGetGPWithAssociatedGPContractsGenericFaultFaultFaultMessage {
        return null;
    }
}
