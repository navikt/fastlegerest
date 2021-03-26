package no.nav.syfo.consumer.tilgangskontroll

import no.nav.security.oidc.context.OIDCRequestContextHolder
import no.nav.syfo.services.TilgangService
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.web.client.RestTemplate

@RunWith(MockitoJUnitRunner::class)
class TilgangServiceTest {
    private lateinit var tilgangService: TilgangService

    @Mock
    private lateinit var restTemplate: RestTemplate

    @Mock
    private lateinit var contextHolder: OIDCRequestContextHolder

    @Test
    fun godkjennMocketTilgangMedInternAzureAD() {
        tilgangService = TilgangService(TILGANGSKONTROLL_URL, HAR_LOCAL_MOCK, restTemplate, contextHolder)
        val tilgang = tilgangService.sjekkTilgang(FNR).harTilgang
        Assertions.assertThat(tilgang).isTrue
    }

    companion object {
        const val FNR = "99999900000"
        private const val HAR_LOCAL_MOCK = true
        private const val TILGANGSKONTROLL_URL = "http://www.nav.no"
    }
}
