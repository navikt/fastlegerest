package no.nav.syfo.fastlege.exception

class FastlegeIkkeFunnet : RuntimeException(FASTLEGEIKKEFUNNET_MSG_DEFAULT) {
    companion object {
        const val FASTLEGEIKKEFUNNET_MSG_DEFAULT = "Fant ikke aktiv fastlege"
    }
}
