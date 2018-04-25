package no.nav.syfo.domain.oppfolgingsplan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RSOppfolgingsplan {
    private String sykmeldtFnr;
    private byte[] oppfolgingsplanPdf;
}
