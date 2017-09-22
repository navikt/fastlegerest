package no.nav.syfo.domain;

import java.time.LocalDate;

public class Pasientforhold {

    public LocalDate fom;
    public LocalDate tom;

    public Pasientforhold withFom(LocalDate fom) {
        this.fom = fom;
        return this;
    }

    public Pasientforhold withTom(LocalDate tom) {
        this.tom = tom;
        return this;
    }
}
