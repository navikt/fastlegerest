package no.nav.syfo.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Fastlege {
    public String navn;
    public String fnr;
    public Pasient pasient;
    public Fastlegekontor fastlegekontor;
    public Pasientforhold pasientforhold;
}
