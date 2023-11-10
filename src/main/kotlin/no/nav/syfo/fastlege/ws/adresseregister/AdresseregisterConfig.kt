package no.nav.syfo.fastlege.ws.adresseregister

import no.nav.syfo.fastlege.ws.util.*
import no.nhn.register.communicationparty.ICommunicationPartyService

fun adresseregisterSoapClient(
    serviceUrl: String,
    username: String,
    password: String,
) = createPort<ICommunicationPartyService>(serviceUrl) {
    proxy {}
    port { withBasicAuth(username, password) }
}
