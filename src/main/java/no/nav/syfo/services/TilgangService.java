package no.nav.syfo.services;

import org.springframework.cache.annotation.Cacheable;

import javax.ws.rs.client.Client;

import static java.lang.System.getProperty;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;

public class TilgangService {

    private Client client = newClient();

    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public boolean sjekkTilgang(String fnr) {
        if ("true".equals(getProperty("LOCAL_MOCK"))) {
            return true;
        }
        String ssoToken = getSubjectHandler().getInternSsoToken();
        return client.target(getProperty("TILGANGSKONTROLLAPI_URL") + "/tilgangtilbruker")
                .queryParam("fnr", fnr)
                .request(APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + ssoToken)
                .get().getStatus() == 200;
    }

    @Cacheable(value = "tilgang", keyGenerator = "userkeygenerator")
    public boolean harTilgangTilTjenesten() {
        if ("true".equals(getProperty("LOCAL_MOCK"))) {
            return true;
        }
        String ssoToken = getSubjectHandler().getInternSsoToken();
        return client.target(getProperty("TILGANGSKONTROLLAPI_URL") + "/tilgangtiltjenesten")
                .request(APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + ssoToken)
                .get().getStatus() == 200;
    }

}
