package no.nav.syfo.consumer.ws.adresseregister

import no.nav.syfo.consumer.util.ws.LogErrorHandler
import no.nav.syfo.consumer.ws.util.*
import no.nhn.register.communicationparty.ICommunicationPartyService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.*

@Configuration
class AdresseregisterConfig {
    @Bean
    @Primary
    @ConditionalOnProperty(value = ["mockAdresseregisteretV1"], havingValue = "false", matchIfMissing = true)
    fun adresseregisterSoapClient(
        @Value("\${ekstern.helse.adresseregisteret.v1.url}") serviceUrl: String): ICommunicationPartyService {
        val port = WsClient<ICommunicationPartyService>()
            .createPort(serviceUrl, ICommunicationPartyService::class.java, listOf(LogErrorHandler()))
        STSClientConfig.configureRequestSamlToken(port)
        return port
    }
}
