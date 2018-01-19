package no.nav.syfo.services;

import org.springframework.cache.annotation.Cacheable;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.System.getProperty;
import static javax.ws.rs.client.ClientBuilder.newClient;

public class TilgangService {

//    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public Response sjekkTilgang(String fnr) {
        return newClient().target(getProperty("syfo-tilgangskontroll_tilgangtilbruker.url"))
                .queryParam("fnr", fnr)
                .request(MediaType.APPLICATION_JSON)
                .get();
    }

//    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public Response harTilgangTilTjenesten() {
        return newClient().target(getProperty("syfo-tilgangskontroll_tilgangtiltjenesten.url"))
                .request(MediaType.APPLICATION_JSON)
                .get();
    }

}
