package no.nav.syfo.services;

import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.syfo.LocalApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = LocalApplication.class)
public class TilgangServiceTest {

    public static final String FNR = "***REMOVED***";
    private static boolean HAR_LOCAL_MOCK = true;
    private static String TILGANGSKONTROLL_URL = "http://www.nav.no";
    private TilgangService tilgangService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private OIDCRequestContextHolder contextHolder;

    @Test
    public void godkjennMocketTilgang() {
        tilgangService = new TilgangService(TILGANGSKONTROLL_URL, HAR_LOCAL_MOCK, restTemplate, contextHolder);

        Boolean tilgang = tilgangService.sjekkTilgang(FNR);

        assertThat(tilgang).isTrue();
    }

}