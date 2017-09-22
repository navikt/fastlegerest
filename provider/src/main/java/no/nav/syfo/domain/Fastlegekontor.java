package no.nav.syfo.domain;

public class Fastlegekontor {

    public String navn;
    public String adresse;
    public String telefon;
    public String epost;
    public Integer orgnummer;

    public Fastlegekontor withNavn(String navn) {
        this.navn = navn;
        return this;
    }

    public Fastlegekontor withAdresse(String adresse) {
        this.adresse = adresse;
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
