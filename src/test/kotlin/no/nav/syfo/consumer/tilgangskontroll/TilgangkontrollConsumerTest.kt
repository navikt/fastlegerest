package no.nav.syfo.consumer.tilgangskontroll

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.web.client.RestTemplate

@RunWith(MockitoJUnitRunner::class)
class TilgangkontrollConsumerTest {
    private lateinit var tilgangkontrollConsumer: TilgangkontrollConsumer

    @Mock
    private lateinit var restTemplate: RestTemplate

    @Mock
    private lateinit var contextHolder: TokenValidationContextHolder

    @Test
    fun godkjennMocketTilgangMedInternAzureAD() {
        tilgangkontrollConsumer = TilgangkontrollConsumer(TILGANGSKONTROLL_URL, HAR_LOCAL_MOCK, restTemplate, contextHolder)
        val tilgang = tilgangkontrollConsumer.sjekkTilgang(FNR).harTilgang
        Assertions.assertThat(tilgang).isTrue
    }

    companion object {
        const val FNR = "99999900000"
        private const val HAR_LOCAL_MOCK = true
        private const val TILGANGSKONTROLL_URL = "http://www.nav.no"
    }
}
