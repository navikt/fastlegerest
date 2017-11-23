package no.nav.syfo.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.syfo.mocks.BrukerprofilMock;
import no.nav.syfo.mocks.EgenansattMock;
import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class EgenAnsattConfig {
    private static final String MOCK_KEY = "egenansatt.withmock";
    private static final String ENDEPUNKT_URL = getProperty("egenAnsatt.endpoint.url");
    private static final String ENDEPUNKT_NAVN = "EGENANSATT_V1";
    private static final boolean KRITISK = true;

    @Bean
    public EgenAnsattV1 egenAnsattV1() {
        EgenAnsattV1 prod = factory().configureStsForOnBehalfOfWithJWT().build();
        EgenAnsattV1 mock = new EgenansattMock();

        return createMetricsProxyWithInstanceSwitcher(ENDEPUNKT_NAVN, prod, mock, MOCK_KEY, EgenAnsattV1.class);
    }

    @Bean
    public Pingable ping() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);
        final EgenAnsattV1 brukerprofilV3 = factory()
                .configureStsForSystemUserInFSS()
                .build();
        return () -> {
            try {
                brukerprofilV3.ping();
                return lyktes(pingMetadata);
            } catch (Exception e) {
                return feilet(pingMetadata, e);
            }
        };
    }

    private CXFClient<EgenAnsattV1> factory() {
        return new CXFClient<>(EgenAnsattV1.class).address(ENDEPUNKT_URL);
    }
}

