package no.nav.syfo.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Optional;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Fastlegekontor {
    public String navn;
    public Adresse besoeksadresse;
    public Adresse postadresse;
    public String telefon;
    public String epost;
    public String orgnummer;

    public String postadresseToString() {
        return Optional.ofNullable(postadresse)
                .map(adr -> "Postboks " + adr.adresse() + ", " + adr.postnummer() + " " + adr.poststed())
                .orElse("");
    }

    public String besoeksadresseToString() {
        return Optional.ofNullable(besoeksadresse)
                .map(adr -> adr.adresse() + ", " + adr.postnummer() + " " + adr.poststed())
                .orElse("");
    }
}
