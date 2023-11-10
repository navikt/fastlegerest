package no.nav.syfo.fastlege.ws.fastlegeregister

import no.nav.syfo.fastlege.ws.util.createPort
import no.nhn.schemas.reg.flr.IFlrReadOperations

fun fastlegeSoapClient(
    serviceUrl: String,
    username: String,
    password: String,
) = createPort<IFlrReadOperations>(serviceUrl) {
    proxy {}
    port { withBasicAuth(username, password) }
}
