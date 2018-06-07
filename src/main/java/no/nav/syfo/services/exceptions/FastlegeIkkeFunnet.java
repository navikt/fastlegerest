package no.nav.syfo.services.exceptions;

import no.nav.apiapp.feil.Feil;

public class FastlegeIkkeFunnet extends Feil {

    public FastlegeIkkeFunnet(String message) {
        super(Type.UKJENT, message);
    }
}
