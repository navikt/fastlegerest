package no.nav.syfo.testhelper.mock

import com.microsoft.schemas._2003._10.serialization.arrays.WSArrayOfint
import no.nav.syfo.testhelper.UserConstants.HER_ID
import no.nav.syfo.testhelper.UserConstants.PARENT_HER_ID
import no.nhn.register.certificatedetails.WSArrayOfCertificateDetails
import no.nhn.register.certificatedetails.WSCertificateDetails
import no.nhn.register.certificatesearchresult.WSArrayOfLdapSearchProvider
import no.nhn.register.certificatesearchresult.WSCertificateSearchResult
import no.nhn.register.common.*
import no.nhn.register.communicationparty.*
import org.datacontract.schemas._2004._07.system_collections.WSArrayOfKeyValuePairOfintArrayOfCodeUO9UWExd
import javax.xml.datatype.XMLGregorianCalendar

class AdresseregisterMock : ICommunicationPartyService {
    override fun getOrganizationPersonDetails(herId: Int?): WSOrganizationPerson {
        if (herId != HER_ID) {
            throw ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage()
        }
        return WSOrganizationPerson()
            .withParentHerId(PARENT_HER_ID)
    }

    override fun getOrganizationDetails(herId: Int?): WSOrganization {
        TODO("Not yet implemented")
    }

    override fun updateDepartmentDetails(org: WSDepartmentUpdate?) {
        TODO("Not yet implemented")
    }

    override fun getServiceDetails(herId: Int?): no.nhn.register.communicationparty.WSService {
        TODO("Not yet implemented")
    }

    override fun search(search: WSCommunicationPartySearch?): WSSearchResult {
        TODO("Not yet implemented")
    }

    override fun getCertificateForValidatingSignatureLdap(ldapUrl: String?): ByteArray {
        TODO("Not yet implemented")
    }

    override fun updateOrganizationIKSAssociations(organizaitonHerId: Int?, herIdsForAssociatedIKS: WSArrayOfint?) {
        TODO("Not yet implemented")
    }

    override fun enableTransportOnOrganization(herId: Int?, transportType: String?) {
        TODO("Not yet implemented")
    }

    override fun getAssosiatedIKSParentOrganizations(herId: Int?): WSArrayOfOrganization {
        TODO("Not yet implemented")
    }

    override fun getCustomAttributes(herIds: WSArrayOfint?): WSArrayOfKeyValuePairOfintArrayOfCodeUO9UWExd {
        TODO("Not yet implemented")
    }

    override fun getCommunicationPartyStatistics(): WSCommunicationPartyStatistics {
        TODO("Not yet implemented")
    }

    override fun getCommunicationPartyDetails(herId: Int?): WSCommunicationParty {
        TODO("Not yet implemented")
    }

    override fun getOrganizationsHavingCode(code: WSCode?): WSArrayOfOrganization {
        TODO("Not yet implemented")
    }

    override fun getCertificateDetailsForEncryption(herId: Int?): WSCertificateDetails {
        TODO("Not yet implemented")
    }

    override fun addCustomAttribute(herId: Int?, code: WSCode?): WSCode {
        TODO("Not yet implemented")
    }

    override fun searchCertificatesByLdapUrl(ldapUrl: String?): WSCertificateSearchResult {
        TODO("Not yet implemented")
    }

    override fun getCertificateForEncryptionByOrganizationNumber(organizationNumber: Int?): ByteArray {
        TODO("Not yet implemented")
    }

    override fun searchById(id: String?): WSArrayOfCommunicationParty {
        TODO("Not yet implemented")
    }

    override fun getCertificateForEncryptionLdap(ldapUrl: String?): ByteArray {
        TODO("Not yet implemented")
    }

    override fun getCertificateForValidatingSignature(herId: Int?): ByteArray {
        TODO("Not yet implemented")
    }

    override fun updateOrganizationPersonDetails(p: WSOrganizationPersonUpdate?) {
        TODO("Not yet implemented")
    }

    override fun updateOrganizationDetails(org: WSOrganizationUpdate?) {
        TODO("Not yet implemented")
    }

    override fun createService(t: WSServiceCreate?): no.nhn.register.communicationparty.WSService {
        TODO("Not yet implemented")
    }

    override fun getCertificateDetailsForValidatingSignature(herId: Int?): WSCertificateDetails {
        TODO("Not yet implemented")
    }

    override fun getOrganizationsOwnedBy(herId: Int?): WSArrayOfOrganization {
        TODO("Not yet implemented")
    }

    override fun ping(): String {
        TODO("Not yet implemented")
    }

    override fun getOrganizationUpdateStatistics(
        fromDate: XMLGregorianCalendar?,
        toDate: XMLGregorianCalendar?
    ): WSOrganizationUpdateStatistics {
        TODO("Not yet implemented")
    }

    override fun searchCertificatesForValidatingSignature(
        sn: String?,
        cn: String?,
        ou: String?
    ): WSArrayOfCertificateDetails {
        TODO("Not yet implemented")
    }

    override fun updateServiceDetails(t: WSServiceUpdate?) {
        TODO("Not yet implemented")
    }

    override fun getCertificate(ldapUrl: String?): ByteArray {
        TODO("Not yet implemented")
    }

    override fun getCertificateForEncryption(herId: Int?): ByteArray {
        TODO("Not yet implemented")
    }

    override fun getLdapSearchProviders(): WSArrayOfLdapSearchProvider {
        TODO("Not yet implemented")
    }

    override fun updateCoverageAreas(herId: Int?, coverageAreas: WSArrayOfCoverageInfo?) {
        TODO("Not yet implemented")
    }

    override fun getCertificateForValidatingSignatureByOrganizationNumber(organizationNumber: Int?): ByteArray {
        TODO("Not yet implemented")
    }

    override fun getDepartmentDetails(herId: Int?): WSDepartment {
        TODO("Not yet implemented")
    }

    override fun searchCertificatesForEncryption(sn: String?, cn: String?, ou: String?): WSArrayOfCertificateDetails {
        TODO("Not yet implemented")
    }

    override fun getAssosiatedIKSChildOrganizations(herId: Int?): WSArrayOfOrganization {
        TODO("Not yet implemented")
    }

    override fun createOrganization(org: WSOrganizationCreate?): WSOrganization {
        TODO("Not yet implemented")
    }

    override fun getChangeLog(herId: Int?): WSArrayOfEntityLogEntry {
        TODO("Not yet implemented")
    }

    override fun createOrganizationPerson(p: WSOrganizationPersonCreate?): WSOrganizationPerson {
        TODO("Not yet implemented")
    }

    override fun getChangedCommunicationPartiesInfo(herIds: WSArrayOfint?): WSArrayOfCommunicationPartyLastChangedInfo {
        TODO("Not yet implemented")
    }

    override fun createDepartment(org: WSDepartmentCreate?): WSDepartment {
        TODO("Not yet implemented")
    }

    override fun setCommunicationPartyValid(herId: Int?, period: WSPeriod?) {
        TODO("Not yet implemented")
    }

    override fun getAllHFCommpartiesCsv(): String {
        TODO("Not yet implemented")
    }

    override fun removeCustomAttribute(herId: Int?, code: WSCode?) {
        TODO("Not yet implemented")
    }

    override fun getFallbackCertificates(p0: String): WSCertificateSearchResult {
        TODO("Not yet implemented")
    }
}
