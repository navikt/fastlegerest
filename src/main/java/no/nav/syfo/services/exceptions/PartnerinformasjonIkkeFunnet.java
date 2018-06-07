package no.nav.syfo.services.exceptions;

import no.nav.apiapp.feil.Feil;

public class PartnerinformasjonIkkeFunnet extends Feil {

    public PartnerinformasjonIkkeFunnet(String message) {
        super(Type.UKJENT, message);
    }
}
