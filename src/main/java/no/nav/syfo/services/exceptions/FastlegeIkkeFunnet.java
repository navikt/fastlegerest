package no.nav.syfo.services.exceptions;

public class FastlegeIkkeFunnet extends RuntimeException {

    public static final String FASTLEGEIKKEFUNNET_MSG_DEFAULT = "Fant ikke aktiv fastlege";

    public FastlegeIkkeFunnet() {
        super(FASTLEGEIKKEFUNNET_MSG_DEFAULT);
    }

    public FastlegeIkkeFunnet(String message) {
        super(message);
    }
}
