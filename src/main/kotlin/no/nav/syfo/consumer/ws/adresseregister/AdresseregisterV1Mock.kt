package no.nav.syfo.consumer.ws.adresseregister

import com.microsoft.schemas._2003._10.serialization.arrays.WSArrayOfint
import no.nhn.register.certificatedetails.WSArrayOfCertificateDetails
import no.nhn.register.certificatedetails.WSCertificateDetails
import no.nhn.register.certificatesearchresult.WSArrayOfLdapSearchProvider
import no.nhn.register.certificatesearchresult.WSCertificateSearchResult
import no.nhn.register.common.*
import no.nhn.register.communicationparty.*
import org.datacontract.schemas._2004._07.system_collections.WSArrayOfKeyValuePairOfintArrayOfCodeUO9UWExd
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import javax.xml.datatype.XMLGregorianCalendar

@Service
@ConditionalOnProperty(value = ["mockAdresseregisteretV1"], havingValue = "true")
class AdresseregisterV1Mock : ICommunicationPartyService {
    @Throws(ICommunicationPartyServiceUpdateDepartmentDetailsValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceUpdateDepartmentDetailsGenericFaultFaultFaultMessage::class)
    override fun updateDepartmentDetails(org: WSDepartmentUpdate) {
    }

    @Throws(ICommunicationPartyServiceGetServiceDetailsGenericFaultFaultFaultMessage::class)
    override fun getServiceDetails(herId: Int): WSService? {
        return null
    }

    @Throws(ICommunicationPartyServiceSearchGenericFaultFaultFaultMessage::class)
    override fun search(search: WSCommunicationPartySearch): WSSearchResult? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetOrganizationDetailsGenericFaultFaultFaultMessage::class)
    override fun getOrganizationDetails(herId: Int): WSOrganization {
        return WSOrganization()
            .withDepartments(WSArrayOfDepartment()
                .withDepartments(WSDepartment()
                    .withParentHerId(12345678))
            )
    }

    @Throws(ICommunicationPartyServiceGetCertificateForValidatingSignatureLdapValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceGetCertificateForValidatingSignatureLdapGenericFaultFaultFaultMessage::class)
    override fun getCertificateForValidatingSignatureLdap(ldapUrl: String): ByteArray {
        return ByteArray(0)
    }

    @Throws(ICommunicationPartyServiceUpdateOrganizationIKSAssociationsValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceUpdateOrganizationIKSAssociationsGenericFaultFaultFaultMessage::class)
    override fun updateOrganizationIKSAssociations(organizaitonHerId: Int, herIdsForAssociatedIKS: WSArrayOfint) {
    }

    @Throws(ICommunicationPartyServiceEnableTransportOnOrganizationGenericFaultFaultFaultMessage::class)
    override fun enableTransportOnOrganization(herId: Int, transportType: String) {
    }

    @Throws(ICommunicationPartyServiceGetAssosiatedIKSParentOrganizationsGenericFaultFaultFaultMessage::class)
    override fun getAssosiatedIKSParentOrganizations(herId: Int): WSArrayOfOrganization? {
        return null
    }

    override fun getCustomAttributes(herIds: WSArrayOfint): WSArrayOfKeyValuePairOfintArrayOfCodeUO9UWExd? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetCommunicationPartyStatisticsGenericFaultFaultFaultMessage::class)
    override fun getCommunicationPartyStatistics(): WSCommunicationPartyStatistics? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetCommunicationPartyDetailsGenericFaultFaultFaultMessage::class)
    override fun getCommunicationPartyDetails(herId: Int): WSCommunicationParty? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetOrganizationsHavingCodeGenericFaultFaultFaultMessage::class)
    override fun getOrganizationsHavingCode(code: WSCode): WSArrayOfOrganization? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetCertificateDetailsForEncryptionValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceGetCertificateDetailsForEncryptionGenericFaultFaultFaultMessage::class)
    override fun getCertificateDetailsForEncryption(herId: Int): WSCertificateDetails? {
        return null
    }

    @Throws(ICommunicationPartyServiceAddCustomAttributeGenericFaultFaultFaultMessage::class)
    override fun addCustomAttribute(herId: Int, code: WSCode): WSCode? {
        return null
    }

    @Throws(ICommunicationPartyServiceSearchCertificatesByLdapUrlValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceSearchCertificatesByLdapUrlGenericFaultFaultFaultMessage::class)
    override fun searchCertificatesByLdapUrl(ldapUrl: String): WSCertificateSearchResult? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetCertificateForEncryptionByOrganizationNumberValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceGetCertificateForEncryptionByOrganizationNumberGenericFaultFaultFaultMessage::class)
    override fun getCertificateForEncryptionByOrganizationNumber(organizationNumber: Int): ByteArray {
        return ByteArray(0)
    }

    @Throws(ICommunicationPartyServiceSearchByIdGenericFaultFaultFaultMessage::class)
    override fun searchById(id: String): WSArrayOfCommunicationParty? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetCertificateForEncryptionLdapValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceGetCertificateForEncryptionLdapGenericFaultFaultFaultMessage::class)
    override fun getCertificateForEncryptionLdap(ldapUrl: String): ByteArray {
        return ByteArray(0)
    }

    @Throws(ICommunicationPartyServiceGetCertificateForValidatingSignatureValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceGetCertificateForValidatingSignatureGenericFaultFaultFaultMessage::class)
    override fun getCertificateForValidatingSignature(herId: Int): ByteArray {
        return ByteArray(0)
    }

    @Throws(ICommunicationPartyServiceUpdateOrganizationPersonDetailsValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceUpdateOrganizationPersonDetailsGenericFaultFaultFaultMessage::class)
    override fun updateOrganizationPersonDetails(p: WSOrganizationPersonUpdate) {
    }

    @Throws(ICommunicationPartyServiceUpdateOrganizationDetailsValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceUpdateOrganizationDetailsGenericFaultFaultFaultMessage::class)
    override fun updateOrganizationDetails(org: WSOrganizationUpdate) {
    }

    @Throws(ICommunicationPartyServiceCreateServiceValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceCreateServiceGenericFaultFaultFaultMessage::class)
    override fun createService(t: WSServiceCreate): WSService? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage::class)
    override fun getOrganizationPersonDetails(herId: Int): WSOrganizationPerson? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetCertificateDetailsForValidatingSignatureValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceGetCertificateDetailsForValidatingSignatureGenericFaultFaultFaultMessage::class)
    override fun getCertificateDetailsForValidatingSignature(herId: Int): WSCertificateDetails? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetOrganizationsOwnedByGenericFaultFaultFaultMessage::class)
    override fun getOrganizationsOwnedBy(herId: Int): WSArrayOfOrganization? {
        return null
    }

    override fun ping(): String? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetOrganizationUpdateStatisticsGenericFaultFaultFaultMessage::class)
    override fun getOrganizationUpdateStatistics(fromDate: XMLGregorianCalendar, toDate: XMLGregorianCalendar): WSOrganizationUpdateStatistics? {
        return null
    }

    @Throws(ICommunicationPartyServiceSearchCertificatesForValidatingSignatureValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceSearchCertificatesForValidatingSignatureGenericFaultFaultFaultMessage::class)
    override fun searchCertificatesForValidatingSignature(sn: String, cn: String, ou: String): WSArrayOfCertificateDetails? {
        return null
    }

    @Throws(ICommunicationPartyServiceUpdateServiceDetailsValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceUpdateServiceDetailsGenericFaultFaultFaultMessage::class)
    override fun updateServiceDetails(t: WSServiceUpdate) {
    }

    @Throws(ICommunicationPartyServiceGetCertificateValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceGetCertificateGenericFaultFaultFaultMessage::class)
    override fun getCertificate(ldapUrl: String): ByteArray {
        return ByteArray(0)
    }

    @Throws(ICommunicationPartyServiceGetCertificateForEncryptionValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceGetCertificateForEncryptionGenericFaultFaultFaultMessage::class)
    override fun getCertificateForEncryption(herId: Int): ByteArray {
        return ByteArray(0)
    }

    @Throws(ICommunicationPartyServiceGetLdapSearchProvidersValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceGetLdapSearchProvidersGenericFaultFaultFaultMessage::class)
    override fun getLdapSearchProviders(): WSArrayOfLdapSearchProvider? {
        return null
    }

    override fun updateCoverageAreas(herId: Int, coverageAreas: WSArrayOfCoverageInfo) {}
    @Throws(ICommunicationPartyServiceGetCertificateForValidatingSignatureByOrganizationNumberValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceGetCertificateForValidatingSignatureByOrganizationNumberGenericFaultFaultFaultMessage::class)
    override fun getCertificateForValidatingSignatureByOrganizationNumber(organizationNumber: Int): ByteArray {
        return ByteArray(0)
    }

    @Throws(ICommunicationPartyServiceGetDepartmentDetailsGenericFaultFaultFaultMessage::class)
    override fun getDepartmentDetails(herId: Int): WSDepartment? {
        return null
    }

    @Throws(ICommunicationPartyServiceSearchCertificatesForEncryptionValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceSearchCertificatesForEncryptionGenericFaultFaultFaultMessage::class)
    override fun searchCertificatesForEncryption(sn: String, cn: String, ou: String): WSArrayOfCertificateDetails? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetAssosiatedIKSChildOrganizationsGenericFaultFaultFaultMessage::class)
    override fun getAssosiatedIKSChildOrganizations(herId: Int): WSArrayOfOrganization? {
        return null
    }

    @Throws(ICommunicationPartyServiceCreateOrganizationValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceCreateOrganizationGenericFaultFaultFaultMessage::class)
    override fun createOrganization(org: WSOrganizationCreate): WSOrganization? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetChangeLogGenericFaultFaultFaultMessage::class)
    override fun getChangeLog(herId: Int): WSArrayOfEntityLogEntry? {
        return null
    }

    @Throws(ICommunicationPartyServiceCreateOrganizationPersonValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceCreateOrganizationPersonGenericFaultFaultFaultMessage::class)
    override fun createOrganizationPerson(p: WSOrganizationPersonCreate): WSOrganizationPerson? {
        return null
    }

    @Throws(ICommunicationPartyServiceGetChangedCommunicationPartiesInfoGenericFaultFaultFaultMessage::class)
    override fun getChangedCommunicationPartiesInfo(wsArrayOfint: WSArrayOfint): WSArrayOfCommunicationPartyLastChangedInfo? {
        return null
    }

    @Throws(ICommunicationPartyServiceCreateDepartmentValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceCreateDepartmentGenericFaultFaultFaultMessage::class)
    override fun createDepartment(org: WSDepartmentCreate): WSDepartment? {
        return null
    }

    @Throws(ICommunicationPartyServiceSetCommunicationPartyValidValidationFaultFaultFaultMessage::class, ICommunicationPartyServiceSetCommunicationPartyValidGenericFaultFaultFaultMessage::class)
    override fun setCommunicationPartyValid(herId: Int, period: WSPeriod) {
    }

    override fun getAllHFCommpartiesCsv(): String? {
        return null
    }

    @Throws(ICommunicationPartyServiceRemoveCustomAttributeGenericFaultFaultFaultMessage::class)
    override fun removeCustomAttribute(herId: Int, code: WSCode) {
    }
}
