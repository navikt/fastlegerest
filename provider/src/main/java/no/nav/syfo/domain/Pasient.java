package no.nav.syfo.domain;

public class Pasient {

    public String navn;
    public String fnr;

    public Pasient withNavn(String navn) {
        this.navn = navn;
        return this;
    }

    public Pasient withFnr(String fnr) {
        this.fnr = fnr;
        return this;
    }
}
