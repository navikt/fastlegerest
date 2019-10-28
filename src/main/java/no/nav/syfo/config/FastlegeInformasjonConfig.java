package no.nav.syfo.config;

import no.nav.syfo.consumer.util.ws.LogErrorHandler;
import no.nav.syfo.consumer.util.ws.WsClient;
import no.nhn.schemas.reg.flr.IFlrReadOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import static java.util.Collections.singletonList;
import static no.nav.syfo.consumer.util.ws.STSClientConfig.configureRequestSamlToken;

@Configuration
public class FastlegeInformasjonConfig {


    @Bean
    @Primary
    @ConditionalOnProperty(value="mockEksternHelse", havingValue = "false", matchIfMissing = true)
    public IFlrReadOperations fastlegeSoapClient(
            @Value("${ekstern.helse.fastlegeinformasjon.url}") String serviceUrl) {

        IFlrReadOperations port = new WsClient<IFlrReadOperations>().createPort(
                serviceUrl,
                IFlrReadOperations.class,
                singletonList((new LogErrorHandler())));
        configureRequestSamlToken(port);
        return port;

    }

}
