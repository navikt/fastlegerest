package no.nav.syfo.domain.dialogmelding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RSMeldingInfo {
    private RSMottaker mottaker;
    private RSPasient pasient;
}
