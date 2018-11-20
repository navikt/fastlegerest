package no.nav.syfo.config;

import no.nav.syfo.consumer.util.ws.*;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import static java.util.Collections.singletonList;
import static no.nav.syfo.consumer.util.ws.STSClientConfig.configureRequestSamlToken;

@Configuration
public class BrukerprofilConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(value="mockBrukerprovilV3", havingValue = "false", matchIfMissing = true)
    public BrukerprofilV3 brukerprofilV3(@Value("${brukerprofil.v3.endpointurl}") String serviceUrl) {
        BrukerprofilV3 port = new WsClient<BrukerprofilV3>().createPort(serviceUrl, BrukerprofilV3.class,
                singletonList(new LogErrorHandler()));
        configureRequestSamlToken(port);
        return port;
    }
}
