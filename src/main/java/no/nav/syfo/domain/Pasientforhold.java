package no.nav.syfo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;


@Data
@Accessors(fluent = true)
@EqualsAndHashCode
public class Pasientforhold {
    private LocalDate fom;
    private LocalDate tom;
}