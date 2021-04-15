package no.nav.syfo.consumer.tilgangskontroll

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.consumer.azuread.v2.AzureAdV2TokenConsumer
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

    @Mock
    private lateinit var azureAdV2TokenConsumer: AzureAdV2TokenConsumer

    @Test
    fun godkjennMocketTilgangMedInternAzureAD() {
        tilgangkontrollConsumer = TilgangkontrollConsumer(
            HAR_LOCAL_MOCK,
            TILGANGSKONTROLL_URL,
            "1234567",
            restTemplate,
            azureAdV2TokenConsumer,
            contextHolder
        )
        val tilgang = tilgangkontrollConsumer.sjekkTilgang(FNR).harTilgang
        Assertions.assertThat(tilgang).isTrue
    }

    companion object {
        const val FNR = "99999900000"
        private const val HAR_LOCAL_MOCK = true
        private const val TILGANGSKONTROLL_URL = "http://www.nav.no"
    }
}
