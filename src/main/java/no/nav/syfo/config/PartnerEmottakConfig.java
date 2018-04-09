package no.nav.syfo.config;

import no.nav.emottak.schemas.PartnerResource;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.syfo.mocks.PartnerResourceMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class PartnerEmottakConfig {

    private static final String MOCK_KEY = "partner.emottak.withmock";
    private static final String ENDEPUNKT_URL = getProperty("PARTNER_WS_ENDPOINTURL");
    private static final String ENDEPUNKT_NAVN = "PARTNER_EMOTTAK";

    @Bean
    public PartnerResource partnerResource() {
        PartnerResource mock = new PartnerResourceMock();
        CXFClient<PartnerResource> factory = factory();
        factory.factoryBean.setUsername(getProperty("SRVFASTLEGEREST_USERNAME"));
        factory.factoryBean.setPassword(getProperty("SRVFASTLEGEREST_PASSWORD"));
        PartnerResource prod = factory.build();
        return createMetricsProxyWithInstanceSwitcher(ENDEPUNKT_NAVN, prod, mock, MOCK_KEY, PartnerResource.class);
    }

    private CXFClient<PartnerResource> factory() {
        return new CXFClient<>(PartnerResource.class).address(ENDEPUNKT_URL);
    }
}
