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

    private static final String MOCK_KEY = "fastlegev1.withmock";
    private static final String ENDEPUNKT_URL = getProperty("fastlegeinformasjon.endpoint.url");

    @Bean
    public IFlrReadOperations fastlegeSoapClient() {
        IFlrReadOperations prod = factory().configureStsForSystemUserInFSS().build();
        IFlrReadOperations mock = new FastlegeV1Mock();
        return createMetricsProxyWithInstanceSwitcher("FASTLEGE_V1", prod, mock, MOCK_KEY, IFlrReadOperations.class);
    }

    private CXFClient<IFlrReadOperations> factory() {
        return new CXFClient<>(IFlrReadOperations.class).address(ENDEPUNKT_URL);
    }

}
