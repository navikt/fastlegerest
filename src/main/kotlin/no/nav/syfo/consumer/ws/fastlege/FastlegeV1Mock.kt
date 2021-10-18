package no.nav.syfo.consumer.ws.fastlege

import com.microsoft.schemas._2003._10.serialization.arrays.unngaduplikat.ArrayOflong
import com.microsoft.schemas._2003._10.serialization.arrays.unngaduplikat.ArrayOfstring
import no.nav.syfo.consumer.ws.util.toXMLGregorianCalendar
import no.nhn.register.fastlegeinformasjon.common.*
import no.nhn.schemas.reg.common.en.WSPeriod
import no.nhn.schemas.reg.flr.*
import org.datacontract.schemas._2004._07.nhn_dtocontracts_flr.GetNavPatientListsParameters
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
@ConditionalOnProperty(value = ["mockEksternHelse"], havingValue = "true")
class FastlegeV1Mock : IFlrReadOperations {
    override fun getPatientGPDetails(s: String): WSPatientToGPContractAssociation {
        val fromTime = LocalDateTime.now().minusYears(4).toXMLGregorianCalendar()
        val toTime = LocalDateTime.now().plusYears(2).toXMLGregorianCalendar()
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
                .withFrom(LocalDateTime.now().minusYears(4))
                .withTo(LocalDateTime.now().plusYears(2)))
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
                            .withFrom(LocalDateTime.now().minusYears(4))
                            .withTo(LocalDateTime.now().plusYears(2)))
                ))
    }

    override fun getGPContractForNav(
        doctorNin: String?,
        municipalityNr: String?,
        doSubstituteSearch: Boolean?
    ): WSGPContract {
        TODO("Not yet implemented")
    }

    override fun getGPContractsOnOffice(integer: Int, localDateTime: LocalDateTime): WSArrayOfGPContract? {
        return null
    }

    override fun navGetEncryptedPatientListAlternate(
        doctorNIN: String?,
        municipalityId: String?,
        encryptWithX509Certificate: ByteArray?,
        month: LocalDateTime?,
        doSubstituteSearch: Boolean?,
        senderXml: String?,
        receiverXml: String?,
        listType: String?
    ): ByteArray? {
        TODO("Not yet implemented")
    }

    override fun getPatientsGPDetailsAtTime(wsArrayOfNinWithTimestamp: WSArrayOfNinWithTimestamp): WSArrayOfPatientToGPContractAssociation? {
        return null
    }

    override fun getPatientsGPDetails(patientNins: ArrayOfstring?): WSArrayOfPatientToGPContractAssociation? {
        TODO("Not yet implemented")
    }

    override fun getGPContractIdsOperatingInPostalCode(postNr: String?): ArrayOflong {
        TODO("Not yet implemented")
    }

    override fun navGetEncryptedPatientList(wsNavEncryptedPatientListParameters: WSNavEncryptedPatientListParameters): ByteArray {
        return ByteArray(0)
    }

    override fun searchForGP(searchParameters: WSGPSearchParameters?): WSPagedResultOfGPDetailsREPj1Nec {
        TODO("Not yet implemented")
    }

    override fun getNavPatientLists(parameters: GetNavPatientListsParameters?): ByteArray {
        TODO("Not yet implemented")
    }

    override fun getPatientGPHistory(
        patientNin: String?,
        includePatientData: Boolean?
    ): WSArrayOfPatientToGPContractAssociation {
        TODO("Not yet implemented")
    }

    override fun confirmGP(patientNin: String?, hprNumber: Int?, atTime: LocalDateTime?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getGPContract(gpContractId: Long?): WSGPContract {
        TODO("Not yet implemented")
    }

    override fun queryGPContracts(queryParameters: WSGPContractQueryParameters?): WSPagedResultOfGPContractREPj1Nec {
        TODO("Not yet implemented")
    }

    override fun getPrimaryHealthCareTeam(id: Long?): WSPrimaryHealthCareTeam {
        TODO("Not yet implemented")
    }

    override fun getGPPatientList(aLong: Long): WSArrayOfPatientToGPContractAssociation? {
        return null
    }

    override fun getGPWithAssociatedGPContracts(integer: Int, localDateTime: LocalDateTime): WSGPDetails? {
        return null
    }
}
