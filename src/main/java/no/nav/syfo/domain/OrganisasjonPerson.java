package no.nav.syfo.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode
@AllArgsConstructor
public class OrganisasjonPerson {
    private Integer foreldreEnhetHerId;
}
