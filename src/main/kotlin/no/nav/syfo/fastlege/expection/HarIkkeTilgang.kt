package no.nav.syfo.fastlege.expection

import javax.ws.rs.ForbiddenException

class HarIkkeTilgang(message: String?) : ForbiddenException(message)
