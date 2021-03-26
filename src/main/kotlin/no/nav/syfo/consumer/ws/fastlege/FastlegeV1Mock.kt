package no.nav.syfo.consumer.ws.fastlege

import no.nav.syfo.consumer.ws.util.toXMLGregorianCalendar
import no.nhn.register.fastlegeinformasjon.common.*
import no.nhn.schemas.reg.common.en.WSPeriod
import no.nhn.schemas.reg.flr.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
@ConditionalOnProperty(value = ["mockEksternHelse"], havingValue = "true")
class FastlegeV1Mock : IFlrReadOperations {
    override fun getPatientGPDetails(s: String): WSPatientToGPContractAssociation {
        return WSPatientToGPContractAssociation()
            .withGPContract(WSGPContract()
                .withGPOffice(WSGPOffice()
                    .withName("MOCKDATA: Testkontoret")
                    .withOrganizationNumber(123456789)
                    .withElectronicAddresses(WSArrayOfElectronicAddress().withElectronicAddresses(Arrays.asList(
                        WSElectronicAddress()
                            .withType(WSCode()
                                .withCodeValue("E_TLF")
                            )
                            .withAddress("MOCKDATA: 22229999"),
                        WSElectronicAddress()
                            .withType(WSCode()
                                .withCodeValue("E_EDI")
                            )
                            .withAddress("MOCKDATA: test@testkontoret.no")
                    )))
                    .withPhysicalAddresses(WSArrayOfPhysicalAddress().withPhysicalAddresses(Arrays.asList(
                        WSPhysicalAddress()
                            .withType(WSCode()
                                .withCodeValue("PST")
                                .withActive(true)
                            )
                            .withPostbox("MOCKDATA: Olav Rhyes Plass")
                            .withPostalCode(1177)
                            .withCity("MOCKDATA: Oslo"),
                        WSPhysicalAddress()
                            .withType(WSCode()
                                .withCodeValue("RES")
                                .withActive(true)
                            )
                            .withStreetAddress("MOCKDATA: Sannergata 2")
                            .withPostalCode(1177)
                            .withCity("MOCKDATA: Oslo")
                    )))
                )
            )
            .withPeriod(WSPeriod()
                .withFrom(LocalDateTime.now().minusYears(4).toXMLGregorianCalendar())
                .withTo(LocalDateTime.now().plusYears(2).toXMLGregorianCalendar()))
            .withPatient(WSPerson()
                .withFirstName("MOCKDATA: Sygve Sykmeldt"))
            .withDoctorCycles(WSArrayOfGPOnContractAssociation()
                .withGPOnContractAssociations(
                    WSGPOnContractAssociation()
                        .withHprNumber(12341234)
                        .withGP(WSPerson()
                            .withFirstName("MOCKDATA: Lege")
                            .withLastName("MOCKDATA: Legesen")
                            .withNIN("MOCKDATA: 12312312312"))
                        .withValid(WSPeriod()
                            .withFrom(LocalDateTime.now().minusYears(4).toXMLGregorianCalendar())
                            .withTo(LocalDateTime.now().plusYears(2).toXMLGregorianCalendar()))
                ))
    }

    override fun getGPContractsOnOffice(integer: Int, localDateTime: LocalDateTime): WSArrayOfGPContract? {
        return null
    }

    override fun getPatientsGPDetailsAtTime(wsArrayOfNinWithTimestamp: WSArrayOfNinWithTimestamp): WSArrayOfPatientToGPContractAssociation? {
        return null
    }

    override fun navGetEncryptedPatientList(wsNavEncryptedPatientListParameters: WSNavEncryptedPatientListParameters): ByteArray {
        return ByteArray(0)
    }

    override fun getGPPatientList(aLong: Long): WSArrayOfPatientToGPContractAssociation? {
        return null
    }

    override fun getGPWithAssociatedGPContracts(integer: Int, localDateTime: LocalDateTime): WSGPDetails? {
        return null
    }
}
