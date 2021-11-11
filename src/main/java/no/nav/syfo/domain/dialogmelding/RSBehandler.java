package no.nav.syfo.domain.dialogmelding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RSBehandler {
    private String fnr;
    private Integer hprId;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
}
