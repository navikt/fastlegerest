package no.nav.syfo.domain.dialogmelding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RSHodemelding {
    private RSMeldingInfo meldingInfo;
    private RSVedlegg vedlegg;
}
