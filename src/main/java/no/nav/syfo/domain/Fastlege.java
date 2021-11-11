package no.nav.syfo.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Fastlege {
    public String fornavn;
    public String mellomnavn;
    public String etternavn;
    public String fnr;
    public Integer herId;
    public Integer foreldreEnhetHerId;
    public Integer helsepersonellregisterId;
    public Pasient pasient;
    public Fastlegekontor fastlegekontor;
    public Pasientforhold pasientforhold;
}
