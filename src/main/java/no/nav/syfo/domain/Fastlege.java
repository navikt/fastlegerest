package no.nav.syfo.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Fastlege {
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private String fnr;
    private String helsepersonellregisterId;
    private Pasient pasient;
    private Fastlegekontor fastlegekontor;
    private Pasientforhold pasientforhold;
}
