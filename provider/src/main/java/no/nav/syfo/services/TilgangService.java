package no.nav.syfo.services;

import no.nav.brukerdialog.security.context.SubjectHandler;
import org.springframework.cache.annotation.Cacheable;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.System.getProperty;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public class TilgangService {

    private Client client = newClient();

    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public Response sjekkTilgang(String fnr) {
        String ssoToken = SubjectHandler.getSubjectHandler().getInternSsoToken();
        return client.target(getProperty("syfo-tilgangskontroll-api.url") + "/tilgangtilbruker")
                .queryParam("fnr", fnr)
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + ssoToken)
                .get();
    }

    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public Response harTilgangTilTjenesten() {
        String ssoToken = SubjectHandler.getSubjectHandler().getInternSsoToken();
        return client.target(getProperty("syfo-tilgangskontroll-api.url") + "/tilgangtiltjenesten")
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + ssoToken)
                .get();
    }

}
