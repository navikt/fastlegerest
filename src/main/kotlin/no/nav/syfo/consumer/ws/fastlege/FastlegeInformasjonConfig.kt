package no.nav.syfo.consumer.ws.fastlege

import no.nav.syfo.consumer.util.ws.LogErrorHandler
import no.nav.syfo.consumer.ws.util.*
import no.nhn.schemas.reg.flr.IFlrReadOperations
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.*

@Configuration
class FastlegeInformasjonConfig {
    @Bean
    @Primary
    @ConditionalOnProperty(value = ["mockEksternHelse"], havingValue = "false", matchIfMissing = true)
    fun fastlegeSoapClient(
        @Value("\${ekstern.helse.fastlegeinformasjon.url}") serviceUrl: String): IFlrReadOperations {
        val port = WsClient<IFlrReadOperations>().createPort(
            serviceUrl,
            IFlrReadOperations::class.java,
            listOf(LogErrorHandler()))
        STSClientConfig.configureRequestSamlToken(port)
        return port
    }
}
