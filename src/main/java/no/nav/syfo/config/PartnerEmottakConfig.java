package no.nav.syfo.config;

import no.nav.emottak.schemas.PartnerResource;
import no.nav.syfo.consumer.util.ws.LogErrorHandler;
import no.nav.syfo.consumer.util.ws.WsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import static java.util.Collections.singletonList;
import static no.nav.syfo.consumer.util.ws.STSClientConfig.configureRequestSamlToken;

@Configuration
public class PartnerEmottakConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(value="mockPartnerEmottak", havingValue = "false", matchIfMissing = true)
    public PartnerResource partnerResource(@Value("${srvfastlegerest.username}") String username,
                                           @Value("${srvfastlegerest.password}") String password,
                                           @Value("${partner.ws.endpointurl}") String serviceUrl ){

        PartnerResource port = new WsClient<PartnerResource>().createPort(
                serviceUrl,
                PartnerResource.class,
                singletonList(new LogErrorHandler()));
        configureRequestSamlToken(port);
        return port;

    }

}
