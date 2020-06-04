package no.nav.syfo.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.Builder;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Pasient {
    public String fornavn;
    public String mellomnavn;
    public String etternavn;
    public String fnr;
}
