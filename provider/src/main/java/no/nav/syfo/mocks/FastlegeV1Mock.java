package no.nav.syfo.mocks;


import no.nhn.schemas.reg.common.en.WSPeriod;
import no.nhn.schemas.reg.flr.*;

import java.time.LocalDateTime;

public class FastlegeV1Mock implements IFlrReadOperations {


    @Override
    public WSPatientToGPContractAssociation getPatientGPDetails(String s) throws IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage {
        return new WSPatientToGPContractAssociation()
                .withPatient(new WSPerson()
                        .withFirstName("Sygve Sykmeldt"))
                .withGPContract(new WSGPContract()
                        .withName("Lars Lege")
                        .withDoctorCycles(new WSArrayOfGPOnContractAssociation()
                                .withGPOnContractAssociations(
                                        new WSGPOnContractAssociation()
                                                .withValid(new WSPeriod())
                                                .withGP(new WSPerson())
                                ))
                );
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
//        return new WSArrayOfPatientToGPContractAssociation().withPatientToGPContractAssociations(Arrays.asList(
//                new WSPatientToGPContractAssociation().withGPContract(new WSGPContract().get)
//        ));
        return null;
    }

    @Override
    public WSGPDetails getGPWithAssociatedGPContracts(Integer integer, LocalDateTime localDateTime) throws IFlrReadOperationsGetGPWithAssociatedGPContractsGenericFaultFaultFaultMessage {
        return null;
    }
}
