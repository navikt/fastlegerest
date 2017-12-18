package no.nav.syfo.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.syfo.mocks.FastlegeV1Mock;
import no.nhn.schemas.reg.flr.IFlrReadOperations;
import no.nhn.schemas.reg.flr.IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;

@Configuration
public class FastlegeInformasjonConfig {

    private static final String MOCK_KEY = "fastlegev1.withmock";
    private static final String ENDEPUNKT_URL = getProperty("fastlegeinformasjon.endpoint.url");
    private static final String ENDEPUNKT_NAVN = "FASTLEGE_HELSENETT";
    private static final boolean KRITISK = true;

    @Bean
    public IFlrReadOperations fastlegeSoapClient() {
        IFlrReadOperations prod = factory().configureStsForSystemUserInFSS().build();
        IFlrReadOperations mock = new FastlegeV1Mock();
        return createMetricsProxyWithInstanceSwitcher("FASTLEGE_V1", prod, mock, MOCK_KEY, IFlrReadOperations.class);
    }

    @Bean
    public Pingable fastlegePing() {
        Pingable.Ping.PingMetadata pingMetadata = new Pingable.Ping.PingMetadata(ENDEPUNKT_URL, ENDEPUNKT_NAVN, KRITISK);
        final IFlrReadOperations pinger = factory()
                .configureStsForSystemUserInFSS()
                .build();
        return () -> {
            try {
                pinger.getPatientGPDetails("***REMOVED***");
                return lyktes(pingMetadata);
            } catch (IFlrReadOperationsGetPatientGPDetailsGenericFaultFaultFaultMessage e) {
                return lyktes(pingMetadata);
            } catch (Exception e) {
                return feilet(pingMetadata, e);
            }
        };
    }

    private CXFClient<IFlrReadOperations> factory() {
        return new CXFClient<>(IFlrReadOperations.class).address(ENDEPUNKT_URL);
    }

}
