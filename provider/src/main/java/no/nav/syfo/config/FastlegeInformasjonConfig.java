package no.nav.syfo.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.syfo.mocks.FastlegeV1Mock;
import no.nhn.schemas.reg.flr.IFlrReadOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class FastlegeInformasjonConfig {

    private static final String FASTLEGE_MOCK_KEY = "fastlegev1.withmock";

    @Bean
    public IFlrReadOperations fastlegeSoapClient() {
        IFlrReadOperations prod = factory().configureStsForOnBehalfOfWithJWT().build();
        IFlrReadOperations mock = new FastlegeV1Mock();

        return createMetricsProxyWithInstanceSwitcher("FASTLEGE_V1", prod, mock, FASTLEGE_MOCK_KEY, IFlrReadOperations.class);
    }

    private CXFClient<IFlrReadOperations> factory() {
        return new CXFClient<>(IFlrReadOperations.class).address(getProperty("helsepersonellv1.endpoint.url"));
    }

}
