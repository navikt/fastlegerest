package no.nav.syfo.services;

import no.nav.brukerdialog.security.context.SubjectHandler;
import org.springframework.cache.annotation.Cacheable;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.lang.System.getProperty;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public class TilgangService {

    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public Response sjekkTilgang(String fnr) {
        String ssoToken = SubjectHandler.getSubjectHandler().getInternSsoToken();
        return newClient().target(getProperty("syfo-tilgangskontroll-api.url") + "/tilgangtilbruker")
                .queryParam("fnr", fnr)
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + ssoToken)
                .get();
    }

    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public Response harTilgangTilTjenesten() {
        String ssoToken = SubjectHandler.getSubjectHandler().getInternSsoToken();
        return newClient().target(getProperty("syfo-tilgangskontroll-api.url") + "/tilgangtiltjenesten")
                .request(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + ssoToken)
                .get();
    }

}
