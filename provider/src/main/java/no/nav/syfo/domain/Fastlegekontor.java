package no.nav.syfo.domain;

public class Fastlegekontor {

    public String navn;
    public String besoeksadresse;
    public String postadresse;
    public String telefon;
    public String epost;
    public Integer orgnummer;

    public Fastlegekontor withNavn(String navn) {
        this.navn = navn;
        return this;
    }

    public Fastlegekontor withBesoeksadresse(String besoeksadresse) {
        this.besoeksadresse = besoeksadresse;
        return this;
    }

    public Fastlegekontor withPostadresse(String postadresse) {
        this.postadresse = postadresse;
        return this;
    }

    public Fastlegekontor withOrgnummer(Integer orgnummer) {
        this.orgnummer = orgnummer;
        return this;
    }

    public Fastlegekontor withTelefon(String telefon) {
        this.telefon = telefon;
        return this;
    }

    public Fastlegekontor withEpost(String epost) {
        this.epost = epost;
        return this;
    }
}
