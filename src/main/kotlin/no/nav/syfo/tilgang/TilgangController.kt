package no.nav.syfo.tilgang

import no.nav.security.oidc.api.ProtectedWithClaims
import no.nav.syfo.api.auth.OIDCIssuer
import no.nav.syfo.services.TilgangService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.inject.Inject

@RestController
@ProtectedWithClaims(issuer = OIDCIssuer.AZURE)
@RequestMapping(value = ["/api/internad/tilgang"])
class TilgangController @Inject constructor(
    private val tilgangService: TilgangService
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun isGrantedAccess(): Boolean {
        return tilgangService.isVeilederGrantedAccessToSYFOWithAD
    }
}
