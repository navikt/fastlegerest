package no.nav.syfo.mocks;


import com.microsoft.schemas._2003._10.serialization.arrays.WSArrayOfint;
import no.nhn.register.certificatedetails.WSArrayOfCertificateDetails;
import no.nhn.register.certificatedetails.WSCertificateDetails;
import no.nhn.register.certificatesearchresult.WSArrayOfLdapSearchProvider;
import no.nhn.register.certificatesearchresult.WSCertificateSearchResult;
import no.nhn.register.common.WSArrayOfEntityLogEntry;
import no.nhn.register.common.WSCode;
import no.nhn.register.common.WSPeriod;
import no.nhn.register.communicationparty.*;
import org.datacontract.schemas._2004._07.system_collections.WSArrayOfKeyValuePairOfintArrayOfCodeUO9UWExd;

import javax.xml.datatype.XMLGregorianCalendar;

public class AdresseregisterV1Mock implements ICommunicationPartyService {


    @Override
    public void updateDepartmentDetails(WSDepartmentUpdate org) throws ICommunicationPartyServiceUpdateDepartmentDetailsValidationFaultFaultFaultMessage, ICommunicationPartyServiceUpdateDepartmentDetailsGenericFaultFaultFaultMessage {

    }

    @Override
    public WSService getServiceDetails(Integer herId) throws ICommunicationPartyServiceGetServiceDetailsGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSSearchResult search(WSCommunicationPartySearch search) throws ICommunicationPartyServiceSearchGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSOrganization getOrganizationDetails(Integer herId) throws ICommunicationPartyServiceGetOrganizationDetailsGenericFaultFaultFaultMessage {
        return new WSOrganization()
                .withDepartments(new WSArrayOfDepartment()
                        .withDepartments(new WSDepartment()
                                .withParentHerId(12345678))
                );
    }

    @Override
    public byte[] getCertificateForValidatingSignatureLdap(String ldapUrl) throws ICommunicationPartyServiceGetCertificateForValidatingSignatureLdapValidationFaultFaultFaultMessage, ICommunicationPartyServiceGetCertificateForValidatingSignatureLdapGenericFaultFaultFaultMessage {
        return new byte[0];
    }

    @Override
    public void updateOrganizationIKSAssociations(Integer organizaitonHerId, WSArrayOfint herIdsForAssociatedIKS) throws ICommunicationPartyServiceUpdateOrganizationIKSAssociationsValidationFaultFaultFaultMessage, ICommunicationPartyServiceUpdateOrganizationIKSAssociationsGenericFaultFaultFaultMessage {

    }

    @Override
    public void enableTransportOnOrganization(Integer herId, String transportType) throws ICommunicationPartyServiceEnableTransportOnOrganizationGenericFaultFaultFaultMessage {

    }

    @Override
    public WSArrayOfOrganization getAssosiatedIKSParentOrganizations(Integer herId) throws ICommunicationPartyServiceGetAssosiatedIKSParentOrganizationsGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSArrayOfKeyValuePairOfintArrayOfCodeUO9UWExd getCustomAttributes(WSArrayOfint herIds) {
        return null;
    }

    @Override
    public WSCommunicationPartyStatistics getCommunicationPartyStatistics() throws ICommunicationPartyServiceGetCommunicationPartyStatisticsGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSCommunicationParty getCommunicationPartyDetails(Integer herId) throws ICommunicationPartyServiceGetCommunicationPartyDetailsGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSArrayOfOrganization getOrganizationsHavingCode(WSCode code) throws ICommunicationPartyServiceGetOrganizationsHavingCodeGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSCertificateDetails getCertificateDetailsForEncryption(Integer herId) throws ICommunicationPartyServiceGetCertificateDetailsForEncryptionValidationFaultFaultFaultMessage, ICommunicationPartyServiceGetCertificateDetailsForEncryptionGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSCode addCustomAttribute(Integer herId, WSCode code) throws ICommunicationPartyServiceAddCustomAttributeGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSCertificateSearchResult searchCertificatesByLdapUrl(String ldapUrl) throws ICommunicationPartyServiceSearchCertificatesByLdapUrlValidationFaultFaultFaultMessage, ICommunicationPartyServiceSearchCertificatesByLdapUrlGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public byte[] getCertificateForEncryptionByOrganizationNumber(Integer organizationNumber) throws ICommunicationPartyServiceGetCertificateForEncryptionByOrganizationNumberValidationFaultFaultFaultMessage, ICommunicationPartyServiceGetCertificateForEncryptionByOrganizationNumberGenericFaultFaultFaultMessage {
        return new byte[0];
    }

    @Override
    public WSArrayOfCommunicationParty searchById(String id) throws ICommunicationPartyServiceSearchByIdGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public byte[] getCertificateForEncryptionLdap(String ldapUrl) throws ICommunicationPartyServiceGetCertificateForEncryptionLdapValidationFaultFaultFaultMessage, ICommunicationPartyServiceGetCertificateForEncryptionLdapGenericFaultFaultFaultMessage {
        return new byte[0];
    }

    @Override
    public byte[] getCertificateForValidatingSignature(Integer herId) throws ICommunicationPartyServiceGetCertificateForValidatingSignatureValidationFaultFaultFaultMessage, ICommunicationPartyServiceGetCertificateForValidatingSignatureGenericFaultFaultFaultMessage {
        return new byte[0];
    }

    @Override
    public void updateOrganizationPersonDetails(WSOrganizationPersonUpdate p) throws ICommunicationPartyServiceUpdateOrganizationPersonDetailsValidationFaultFaultFaultMessage, ICommunicationPartyServiceUpdateOrganizationPersonDetailsGenericFaultFaultFaultMessage {

    }

    @Override
    public void updateOrganizationDetails(WSOrganizationUpdate org) throws ICommunicationPartyServiceUpdateOrganizationDetailsValidationFaultFaultFaultMessage, ICommunicationPartyServiceUpdateOrganizationDetailsGenericFaultFaultFaultMessage {

    }

    @Override
    public WSService createService(WSServiceCreate t) throws ICommunicationPartyServiceCreateServiceValidationFaultFaultFaultMessage, ICommunicationPartyServiceCreateServiceGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSOrganizationPerson getOrganizationPersonDetails(Integer herId) throws ICommunicationPartyServiceGetOrganizationPersonDetailsGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSCertificateDetails getCertificateDetailsForValidatingSignature(Integer herId) throws ICommunicationPartyServiceGetCertificateDetailsForValidatingSignatureValidationFaultFaultFaultMessage, ICommunicationPartyServiceGetCertificateDetailsForValidatingSignatureGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSArrayOfOrganization getOrganizationsOwnedBy(Integer herId) throws ICommunicationPartyServiceGetOrganizationsOwnedByGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSCommunicationParty getGPCommunicationParty(String ssn) throws ICommunicationPartyServiceGetGPCommunicationPartyGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public String ping() {
        return null;
    }

    @Override
    public WSOrganizationUpdateStatistics getOrganizationUpdateStatistics(XMLGregorianCalendar fromDate, XMLGregorianCalendar toDate) throws ICommunicationPartyServiceGetOrganizationUpdateStatisticsGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSArrayOfCertificateDetails searchCertificatesForValidatingSignature(String sn, String cn, String ou) throws ICommunicationPartyServiceSearchCertificatesForValidatingSignatureValidationFaultFaultFaultMessage, ICommunicationPartyServiceSearchCertificatesForValidatingSignatureGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public void updateServiceDetails(WSServiceUpdate t) throws ICommunicationPartyServiceUpdateServiceDetailsValidationFaultFaultFaultMessage, ICommunicationPartyServiceUpdateServiceDetailsGenericFaultFaultFaultMessage {

    }

    @Override
    public byte[] getCertificate(String ldapUrl) throws ICommunicationPartyServiceGetCertificateValidationFaultFaultFaultMessage, ICommunicationPartyServiceGetCertificateGenericFaultFaultFaultMessage {
        return new byte[0];
    }

    @Override
    public byte[] getCertificateForEncryption(Integer herId) throws ICommunicationPartyServiceGetCertificateForEncryptionValidationFaultFaultFaultMessage, ICommunicationPartyServiceGetCertificateForEncryptionGenericFaultFaultFaultMessage {
        return new byte[0];
    }

    @Override
    public WSArrayOfLdapSearchProvider getLdapSearchProviders() throws ICommunicationPartyServiceGetLdapSearchProvidersValidationFaultFaultFaultMessage, ICommunicationPartyServiceGetLdapSearchProvidersGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public void updateCoverageAreas(Integer herId, WSArrayOfCoverageInfo coverageAreas) {

    }

    @Override
    public byte[] getCertificateForValidatingSignatureByOrganizationNumber(Integer organizationNumber) throws ICommunicationPartyServiceGetCertificateForValidatingSignatureByOrganizationNumberValidationFaultFaultFaultMessage, ICommunicationPartyServiceGetCertificateForValidatingSignatureByOrganizationNumberGenericFaultFaultFaultMessage {
        return new byte[0];
    }

    @Override
    public WSDepartment getDepartmentDetails(Integer herId) throws ICommunicationPartyServiceGetDepartmentDetailsGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSArrayOfCertificateDetails searchCertificatesForEncryption(String sn, String cn, String ou) throws ICommunicationPartyServiceSearchCertificatesForEncryptionValidationFaultFaultFaultMessage, ICommunicationPartyServiceSearchCertificatesForEncryptionGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSArrayOfOrganization getAssosiatedIKSChildOrganizations(Integer herId) throws ICommunicationPartyServiceGetAssosiatedIKSChildOrganizationsGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSOrganization createOrganization(WSOrganizationCreate org) throws ICommunicationPartyServiceCreateOrganizationValidationFaultFaultFaultMessage, ICommunicationPartyServiceCreateOrganizationGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSArrayOfEntityLogEntry getChangeLog(Integer herId) throws ICommunicationPartyServiceGetChangeLogGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSOrganizationPerson createOrganizationPerson(WSOrganizationPersonCreate p) throws ICommunicationPartyServiceCreateOrganizationPersonValidationFaultFaultFaultMessage, ICommunicationPartyServiceCreateOrganizationPersonGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public WSDepartment createDepartment(WSDepartmentCreate org) throws ICommunicationPartyServiceCreateDepartmentValidationFaultFaultFaultMessage, ICommunicationPartyServiceCreateDepartmentGenericFaultFaultFaultMessage {
        return null;
    }

    @Override
    public void setCommunicationPartyValid(Integer herId, WSPeriod period) throws ICommunicationPartyServiceSetCommunicationPartyValidValidationFaultFaultFaultMessage, ICommunicationPartyServiceSetCommunicationPartyValidGenericFaultFaultFaultMessage {

    }

    @Override
    public String getAllHFCommpartiesCsv() {
        return null;
    }

    @Override
    public void removeCustomAttribute(Integer herId, WSCode code) throws ICommunicationPartyServiceRemoveCustomAttributeGenericFaultFaultFaultMessage {

    }
}
