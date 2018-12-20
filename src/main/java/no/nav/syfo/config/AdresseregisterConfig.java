package no.nav.syfo.config;

import no.nav.syfo.consumer.util.ws.LogErrorHandler;
import no.nav.syfo.consumer.util.ws.WsClient;
import no.nhn.register.communicationparty.ICommunicationPartyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

import static java.util.Collections.singletonList;
import static no.nav.syfo.consumer.util.ws.STSClientConfig.configureRequestSamlToken;

@Configuration
public class AdresseregisterConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(value = "mockAdresseregisteretV1", havingValue = "false", matchIfMissing = true)
    public ICommunicationPartyService adresseregisterSoapClient(
            @Value("{$ekstern.helse.adresseregisteret.v1.endpointurl}") String serviceUrl) {

        ICommunicationPartyService port = new WsClient<ICommunicationPartyService>()
                .createPort(serviceUrl, ICommunicationPartyService.class, singletonList(new LogErrorHandler()));

        configureRequestSamlToken(port);
        return port;
    }

}
