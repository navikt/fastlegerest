package no.nav.syfo.fastlege.expection

class FastlegevikarIkkeFunnet : RuntimeException(FASTLEGEVIKARIKKEFUNNET_MSG_DEFAULT) {
    companion object {
        const val FASTLEGEVIKARIKKEFUNNET_MSG_DEFAULT = "Fant ikke fastlegevikar"
    }
}
