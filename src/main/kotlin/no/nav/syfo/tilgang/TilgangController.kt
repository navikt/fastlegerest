package no.nav.syfo.tilgang

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.consumer.tilgangskontroll.TilgangkontrollConsumer
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@ProtectedWithClaims(issuer = OIDCIssuer.AZURE)
@RequestMapping(value = ["/api/internad/tilgang"])
class TilgangController @Inject constructor(
    private val tilgangkontrollConsumer: TilgangkontrollConsumer
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun isGrantedAccess(): Boolean {
        return tilgangkontrollConsumer.isVeilederGrantedAccessToSYFOWithAD(OIDCIssuer.AZURE)
    }
}
