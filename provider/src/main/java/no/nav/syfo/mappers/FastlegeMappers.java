package no.nav.syfo.mappers;

import no.nav.syfo.domain.Fastlege;
import no.nav.syfo.domain.Fastlegekontor;
import no.nhn.register.common.WSElectronicAddress;
import no.nhn.schemas.reg.flr.WSGPOffice;
import no.nhn.schemas.reg.flr.WSGPOnContractAssociation;

import java.util.function.Function;

public class FastlegeMappers {

    public static Function<WSGPOffice, Fastlegekontor> ws2fastlegekontor = wsgpOffice ->
            new Fastlegekontor()
                    .navn(wsgpOffice.getName())
                    .orgnummer(wsgpOffice.getOrganizationNumber())
                    .telefon(wsgpOffice.getElectronicAddresses().getElectronicAddresses().stream()
                            .filter(wsElectronicAddress -> wsElectronicAddress.getType().getCodeValue().equals("E_TLF"))
                            .map(WSElectronicAddress::getAddress)
                            .findFirst().orElse("")
                    )
                    .epost(wsgpOffice.getElectronicAddresses().getElectronicAddresses().stream()
                            .filter(wsElectronicAddress -> wsElectronicAddress.getType().getCodeValue().equals("E_EDI"))
                            .map(WSElectronicAddress::getAddress)
                            .findFirst().orElse("")
                    )
                    .postadresse(wsgpOffice.getPhysicalAddresses().getPhysicalAddresses().stream()
                            .filter(wsPhysicalAddress -> wsPhysicalAddress.getType().isActive())
                            .filter(wsPhysicalAddress -> "PST".equals(wsPhysicalAddress.getType().getCodeValue()))
                            .map(wsPhysicalAddress -> "Postboks " + wsPhysicalAddress.getPostbox() + ", " + postnummer(wsPhysicalAddress.getPostalCode()) + " " + wsPhysicalAddress.getCity())
                            .findFirst().orElse("")
                    )
                    .besoeksadresse(wsgpOffice.getPhysicalAddresses().getPhysicalAddresses().stream()
                            .filter(wsPhysicalAddress -> wsPhysicalAddress.getType().isActive())
                            .filter(wsPhysicalAddress -> "RES".equals(wsPhysicalAddress.getType().getCodeValue()))
                            .map(wsPhysicalAddress -> wsPhysicalAddress.getStreetAddress() + ", " + postnummer(wsPhysicalAddress.getPostalCode()) + " " + wsPhysicalAddress.getCity())
                            .findFirst().orElse("")
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
                    .navn(wsPatientToGPContractAssociation.getGP().getFirstName() + " " + wsPatientToGPContractAssociation.getGP().getLastName())
                    .fnr(wsPatientToGPContractAssociation.getGP().getNIN());

}
