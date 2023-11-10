package no.nav.syfo.testhelper.mock

import com.microsoft.schemas._2003._10.serialization.arrays.unngaduplikat.ArrayOflong
import com.microsoft.schemas._2003._10.serialization.arrays.unngaduplikat.ArrayOfstring
import no.nav.syfo.testhelper.UserConstants.FASTLEGEKONTOR_ADR
import no.nav.syfo.testhelper.UserConstants.FASTLEGEKONTOR_EPOST
import no.nav.syfo.testhelper.UserConstants.FASTLEGEKONTOR_NAVN
import no.nav.syfo.testhelper.UserConstants.FASTLEGEKONTOR_ORGNR
import no.nav.syfo.testhelper.UserConstants.FASTLEGEKONTOR_POSTBOKS
import no.nav.syfo.testhelper.UserConstants.FASTLEGEKONTOR_POSTNR
import no.nav.syfo.testhelper.UserConstants.FASTLEGEKONTOR_POSTSTED
import no.nav.syfo.testhelper.UserConstants.FASTLEGEKONTOR_TLF
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID_MISSING_HER_ID
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID_MISSING_HPR_NR
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID_MISSING_NIN
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID_MISSING_PST_ADR
import no.nav.syfo.testhelper.UserConstants.FASTLEGEOPPSLAG_PERSON_ID_MISSING_RES_ADR
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_ETTERNAVN
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_FNR
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_FORNAVN
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_HPR_NR
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_RELASJON_KODETEKST
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_RELASJON_KODEVERDI
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_STILLINGSPROSENT
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_ETTERNAVN
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_FNR
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_FORNAVN
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_HPR_NR
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_RELASJON_KODETEKST
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_RELASJON_KODEVERDI
import no.nav.syfo.testhelper.UserConstants.FASTLEGE_VIKAR_STILLINGSPROSENT
import no.nav.syfo.testhelper.UserConstants.HER_ID
import no.nhn.register.fastlegeinformasjon.common.*
import no.nhn.schemas.reg.common.en.WSPeriod
import no.nhn.schemas.reg.flr.*
import org.datacontract.schemas._2004._07.nhn_dtocontracts.FileResponse
import org.datacontract.schemas._2004._07.nhn_dtocontracts_flr.GetNavPatientListsParameters
import java.time.LocalDateTime

class FastlegeMock : IFlrReadOperations {

    override fun getPatientGPDetails(patientNin: String?): WSPatientToGPContractAssociation {
        return if (
            patientNin in arrayOf(
                FASTLEGEOPPSLAG_PERSON_ID,
                FASTLEGEOPPSLAG_PERSON_ID_MISSING_PST_ADR,
                FASTLEGEOPPSLAG_PERSON_ID_MISSING_RES_ADR,
                FASTLEGEOPPSLAG_PERSON_ID_MISSING_HER_ID,
                FASTLEGEOPPSLAG_PERSON_ID_MISSING_HPR_NR,
                FASTLEGEOPPSLAG_PERSON_ID_MISSING_NIN,
            )
        ) {
            WSPatientToGPContractAssociation()
                .withGPHerId(if (patientNin == FASTLEGEOPPSLAG_PERSON_ID_MISSING_HER_ID) null else HER_ID)
                .withPeriod(
                    WSPeriod()
                        .withFrom(LocalDateTime.now().minusYears(2))
                )
                .withGPContract(
                    WSGPContract()
                        .withGPOffice(
                            WSGPOffice()
                                .withName(FASTLEGEKONTOR_NAVN)
                                .withOrganizationNumber(FASTLEGEKONTOR_ORGNR)
                                .withElectronicAddresses(
                                    WSArrayOfElectronicAddress()
                                        .withElectronicAddresses(
                                            WSElectronicAddress()
                                                .withAddress(FASTLEGEKONTOR_TLF)
                                                .withType(
                                                    WSCode().withCodeValue("E_TLF")
                                                ),
                                            WSElectronicAddress()
                                                .withAddress(FASTLEGEKONTOR_EPOST)
                                                .withType(
                                                    WSCode().withCodeValue("E_EDI")
                                                ),
                                        )
                                )
                                .withPhysicalAddresses(
                                    getWsArrayOfPhysicalAddress(patientNin)
                                )
                        )
                )
                .withDoctorCycles(
                    WSArrayOfGPOnContractAssociation()
                        .withGPOnContractAssociations(
                            WSGPOnContractAssociation()
                                .withGP(
                                    WSPerson()
                                        .withDateOfBirth(LocalDateTime.now().minusYears(30))
                                        .withFirstName(FASTLEGE_FORNAVN)
                                        .withLastName(FASTLEGE_ETTERNAVN)
                                        .withNIN(if (patientNin == FASTLEGEOPPSLAG_PERSON_ID_MISSING_NIN) null else FASTLEGE_FNR)
                                )
                                .withHprNumber(if (patientNin == FASTLEGEOPPSLAG_PERSON_ID_MISSING_HPR_NR) null else FASTLEGE_HPR_NR)
                                .withValid(
                                    WSPeriod()
                                        .withFrom(LocalDateTime.now().minusYears(2))
                                )
                                .withRelationship(
                                    WSCode()
                                        .withCodeValue(FASTLEGE_RELASJON_KODEVERDI)
                                        .withCodeText(FASTLEGE_RELASJON_KODETEKST)
                                )
                                .withWorkingPercentage(FASTLEGE_STILLINGSPROSENT),
                            WSGPOnContractAssociation()
                                .withGP(
                                    WSPerson()
                                        .withDateOfBirth(LocalDateTime.now().minusDays(20))
                                        .withFirstName(FASTLEGE_VIKAR_FORNAVN)
                                        .withLastName(FASTLEGE_VIKAR_ETTERNAVN)
                                        .withNIN(if (patientNin == FASTLEGEOPPSLAG_PERSON_ID_MISSING_NIN) null else FASTLEGE_VIKAR_FNR)
                                )
                                .withHprNumber(if (patientNin == FASTLEGEOPPSLAG_PERSON_ID_MISSING_HPR_NR) null else FASTLEGE_VIKAR_HPR_NR)
                                .withValid(
                                    WSPeriod()
                                        .withFrom(LocalDateTime.now().minusYears(2))
                                )
                                .withRelationship(
                                    WSCode()
                                        .withCodeValue(FASTLEGE_VIKAR_RELASJON_KODEVERDI)
                                        .withCodeText(FASTLEGE_VIKAR_RELASJON_KODETEKST)
                                )
                                .withWorkingPercentage(FASTLEGE_VIKAR_STILLINGSPROSENT)
                        )
                )
        } else {
            throw IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage()
        }
    }

    private fun getWsArrayOfPhysicalAddress(patientNin: String?): WSArrayOfPhysicalAddress {
        return when (patientNin) {
            FASTLEGEOPPSLAG_PERSON_ID_MISSING_PST_ADR -> WSArrayOfPhysicalAddress()
                .withPhysicalAddresses(
                    getResWsPhysicalAddress(),
                )
            FASTLEGEOPPSLAG_PERSON_ID_MISSING_RES_ADR -> WSArrayOfPhysicalAddress()
                .withPhysicalAddresses(
                    getPstWsPhysicalAddress(),
                )
            else -> WSArrayOfPhysicalAddress()
                .withPhysicalAddresses(
                    getResWsPhysicalAddress(),
                    getPstWsPhysicalAddress(),
                )
        }
    }

    private fun getPstWsPhysicalAddress() = WSPhysicalAddress()
        .withCity(FASTLEGEKONTOR_POSTSTED)
        .withPostalCode(FASTLEGEKONTOR_POSTNR)
        .withPostbox(FASTLEGEKONTOR_POSTBOKS)
        .withStreetAddress(FASTLEGEKONTOR_ADR)
        .withType(
            WSCode()
                .withCodeValue("PST")
                .withActive(true)
        )

    private fun getResWsPhysicalAddress() = WSPhysicalAddress()
        .withCity(FASTLEGEKONTOR_POSTSTED)
        .withPostalCode(FASTLEGEKONTOR_POSTNR)
        .withStreetAddress(FASTLEGEKONTOR_ADR)
        .withType(
            WSCode()
                .withCodeValue("RES")
                .withActive(true)
        )

    override fun navGetEncryptedPatientListAlternate(
        doctorNIN: String?,
        municipalityId: String?,
        encryptWithX509Certificate: ByteArray?,
        month: LocalDateTime?,
        doSubstituteSearch: Boolean?,
        senderXml: String?,
        receiverXml: String?,
        listType: String?
    ): ByteArray {
        TODO("Not yet implemented")
    }

    override fun getPatientsGPDetailsAtTime(patientNins: WSArrayOfNinWithTimestamp?): WSArrayOfPatientToGPContractAssociation {
        TODO("Not yet implemented")
    }

    override fun getPatientsGPDetails(patientNins: ArrayOfstring?): WSArrayOfPatientToGPContractAssociation {
        TODO("Not yet implemented")
    }

    override fun getGPPatientList(gpContractId: Long?): WSArrayOfPatientToGPContractAssociation {
        TODO("Not yet implemented")
    }

    override fun getGPWithAssociatedGPContracts(hprNumber: Int?, atTime: LocalDateTime?): WSGPDetails {
        TODO("Not yet implemented")
    }

    override fun getGPContractIdsOperatingInPostalCode(postNr: String?): ArrayOflong {
        TODO("Not yet implemented")
    }

    override fun getGPContractsOnOffice(organizationNumber: Int?, atTime: LocalDateTime?): WSArrayOfGPContract {
        TODO("Not yet implemented")
    }

    override fun getGPContractForNav(
        doctorNin: String?,
        municipalityNr: String?,
        doSubstituteSearch: Boolean?
    ): WSGPContract {
        TODO("Not yet implemented")
    }

    override fun navGetEncryptedPatientList(param: WSNavEncryptedPatientListParameters?): ByteArray {
        TODO("Not yet implemented")
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

    override fun getGuestResidentsInMunicipality(p0: String, p1: Int, p2: Int): FileResponse {
        TODO("Not yet implemented")
    }

    override fun getGuestResidentsFromMunicipality(p0: String, p1: Int, p2: Int): FileResponse {
        TODO("Not yet implemented")
    }
}
