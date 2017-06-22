package no.nav.syfo.domain;

import java.time.LocalDate;

public class Fastlege {

    public String navn;
    public String fnr;
    public String adresse;
    public String legekontor;
    public String tlf;
    public String epost;
    public LocalDate fra;
    public LocalDate til;


    public Fastlege withNavn(String navn) {
        this.navn = navn;
        return this;
    }
    public Fastlege withFnr(String fnr) {
        this.fnr = fnr;
        return this;
    }

    public Fastlege withAdresse(String adresse) {
        this.adresse = adresse;
        return this;
    }

    public Fastlege withLegekontor(String legekontor) {
        this.legekontor = legekontor;
        return this;
    }

    public Fastlege withTlf(String tlf) {
        this.tlf = tlf;
        return this;
    }

    public Fastlege withEpost(String epost) {
        this.epost = epost;
        return this;
    }

    public Fastlege withFra(LocalDate fra) {
        this.fra = fra;
        return this;
    }
    public Fastlege withTil(LocalDate til) {
        this.til = til;
        return this;
    }



}
