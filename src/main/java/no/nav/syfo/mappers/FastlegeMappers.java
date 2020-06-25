package no.nav.syfo.mappers;

import no.nav.syfo.domain.*;
import no.nhn.register.fastlegeinformasjon.common.WSElectronicAddress;
import no.nhn.schemas.reg.flr.WSGPOffice;
import no.nhn.schemas.reg.flr.WSGPOnContractAssociation;

import java.util.function.Function;

import static no.nav.syfo.util.MapUtil.mapNullable;

public class FastlegeMappers {

    public static Function<WSGPOffice, Fastlegekontor> ws2fastlegekontor = wsgpOffice ->
            new Fastlegekontor()
                    .navn(wsgpOffice.getName())
                    .orgnummer(mapNullable(wsgpOffice.getOrganizationNumber(), Object::toString))
                    .telefon(wsgpOffice.getElectronicAddresses().getElectronicAddresses().stream()
                            .filter(wsElectronicAddress -> wsElectronicAddress.getType().getCodeValue().equals("E_TLF"))
                            .findFirst()
                            .map(WSElectronicAddress::getAddress)
                            .orElse("")
                    )
                    .epost(wsgpOffice.getElectronicAddresses().getElectronicAddresses().stream()
                            .filter(wsElectronicAddress -> wsElectronicAddress.getType().getCodeValue().equals("E_EDI"))
                            .findFirst()
                            .map(WSElectronicAddress::getAddress)
                            .orElse("")
                    )
                    .postadresse(wsgpOffice.getPhysicalAddresses().getPhysicalAddresses().stream()
                            .filter(wsPhysicalAddress -> wsPhysicalAddress.getType() != null && wsPhysicalAddress.getType().isActive())
                            .filter(wsPhysicalAddress -> "PST".equals(wsPhysicalAddress.getType().getCodeValue()))
                            .findFirst()
                            .map(wsPhysicalAddress -> new Adresse()
                                    .adresse(wsPhysicalAddress.getPostbox())
                                    .postnummer(postnummer(wsPhysicalAddress.getPostalCode()))
                                    .poststed(wsPhysicalAddress.getCity()))
                            .orElse(null)
                    )
                    .besoeksadresse(wsgpOffice.getPhysicalAddresses().getPhysicalAddresses().stream()
                            .filter(wsPhysicalAddress -> wsPhysicalAddress.getType() != null && wsPhysicalAddress.getType().isActive())
                            .filter(wsPhysicalAddress -> "RES".equals(wsPhysicalAddress.getType().getCodeValue()))
                            .findFirst()
                            .map(wsPhysicalAddress -> new Adresse()
                                    .adresse(wsPhysicalAddress.getStreetAddress())
                                    .postnummer(postnummer(wsPhysicalAddress.getPostalCode()))
                                    .poststed(wsPhysicalAddress.getCity()))
                            .orElse(null)
                    );

    private static String postnummer(Integer postalCode) {
        StringBuilder postnummer = new StringBuilder(String.valueOf(postalCode));

        while (postnummer.length() < 4) {
            postnummer.insert(0, "0");
        }
        return postnummer.toString();
    }

    public static Function<WSGPOnContractAssociation, Fastlege> ws2fastlege = wsPatientToGPContractAssociation ->
            new Fastlege()
                    .helsepersonellregisterId(mapNullable(wsPatientToGPContractAssociation.getHprNumber(), Object::toString))
                    .fornavn(wsPatientToGPContractAssociation.getGP().getFirstName())
                    .mellomnavn(wsPatientToGPContractAssociation.getGP().getMiddleName())
                    .etternavn(wsPatientToGPContractAssociation.getGP().getLastName())
                    .fnr(wsPatientToGPContractAssociation.getGP().getNIN());

}
