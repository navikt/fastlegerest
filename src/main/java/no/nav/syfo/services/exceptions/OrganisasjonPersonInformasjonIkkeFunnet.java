package no.nav.syfo.services.exceptions;

import no.nav.apiapp.feil.Feil;

public class OrganisasjonPersonInformasjonIkkeFunnet extends Feil {

    public OrganisasjonPersonInformasjonIkkeFunnet(String message) {
        super(Type.UKJENT, message);
    }
}
