package no.nav.syfo.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.syfo.mocks.AdresseregisterV1Mock;
import no.nhn.register.communicationparty.ICommunicationPartyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class AdresseregisterConfig {

    private static final String MOCK_KEY = "adresseregisterv1.withmock";
    private static final String ENDEPUNKT_URL = getProperty("EKSTERN_HELSE_ADRESSEREGISTERET_ENDPOINTURL");
    private static final String ENDEPUNKT_NAVN = "ADRESSEREGISTER_HELSENETT";
    private static final boolean KRITISK = true;

    @Bean
    public ICommunicationPartyService adresseregisterSoapClient() {
        ICommunicationPartyService prod = factory().configureStsForSystemUserInFSS().build();
        ICommunicationPartyService mock = new AdresseregisterV1Mock();
        return createMetricsProxyWithInstanceSwitcher("ADRESSEREGISTER_V1", prod, mock, MOCK_KEY, ICommunicationPartyService.class);
    }

    @Bean
    public Pingable adresseregisterPing() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);
        final ICommunicationPartyService pinger = factory()
                .configureStsForSystemUserInFSS()
                .build();
        return () -> {
            try {
                pinger.getOrganizationPersonDetails(1345);
                return lyktes(pingMetadata);
            } catch (Exception e) {
                return feilet(pingMetadata, e);
            }
        };
    }

    private CXFClient<ICommunicationPartyService> factory() {
        return new CXFClient<>(ICommunicationPartyService.class).address(ENDEPUNKT_URL);
    }
}
