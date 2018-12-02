package no.nav.syfo.services;

import no.nav.syfo.LocalApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = LocalApplication.class)
public class TilgangServiceTest {

    public static final String FNR = "***REMOVED***";
    private static boolean HAR_LOCAL_MOCK = false;
    private static String TILGANGSKONTROLL_URL = "http://www.nav.no";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TilgangService tilgangService = new TilgangService(TILGANGSKONTROLL_URL, HAR_LOCAL_MOCK, restTemplate);

    @Test
    public void godkjennRiktigTilgang() {
        mockResponseFraTilgangskontroll(HttpStatus.OK);

        Boolean harTilgang = tilgangService.sjekkTilgang("***REMOVED***");

        assertThat(harTilgang).isTrue();
    }

    @Test
    public void avslaFeilTilgang() {
        mockResponseFraTilgangskontroll(HttpStatus.FORBIDDEN);

        Boolean harTilgang = tilgangService.sjekkTilgang(FNR);

        assertThat(harTilgang).isFalse();
    }


    @Test
    public void godkjennMocketTilgang() {
        final boolean lokalMock = true;
        tilgangService = new TilgangService(TILGANGSKONTROLL_URL, lokalMock, restTemplate);

        Boolean tilgang = tilgangService.sjekkTilgang(FNR);

        assertThat(tilgang).isTrue();
    }

    private void mockResponseFraTilgangskontroll(HttpStatus httpStatus){
        ResponseEntity<Object> responseEntity200 = new ResponseEntity<>(httpStatus);
        when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.<HttpMethod> eq(HttpMethod.GET),
                Mockito.<HttpEntity<?>> any(),
                Mockito.<Class<Object>> any()
        )).thenReturn(responseEntity200);
    }
}