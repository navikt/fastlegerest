package no.nav.syfo.domain;

public class Fastlege {
    public String navn;
    public String fnr;
    public Pasient pasient;
    public Fastlegekontor fastlegekontor;
    public Pasientforhold pasientforhold;

    public Fastlege withNavn(String navn) {
        this.navn = navn;
        return this;
    }
    public Fastlege withFnr(String fnr) {
        this.fnr = fnr;
        return this;
    }

    public Fastlege withPasient(Pasient pasient) {
        this.pasient = pasient;
        return this;
    }
    public Fastlege withFastlegekontor(Fastlegekontor fastlegekontor) {
        this.fastlegekontor = fastlegekontor;
        return this;
    }
    public Fastlege withPasientforhold(Pasientforhold pasientforhold) {
        this.pasientforhold = pasientforhold;
        return this;
    }
}
