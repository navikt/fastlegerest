package testhelper

import no.nav.syfo.util.PersonIdent

object UserConstants {
    val ARBEIDSTAKER_PERSONIDENT = PersonIdent("12345678912")
    val ARBEIDSTAKER_PERSONIDENT_VEILEDER_NO_ACCESS = PersonIdent("12345678913")
    val ARBEIDSTAKER_PERSONIDENT_NO_FASTLEGE = PersonIdent("12345678914")
    val ARBEIDSTAKER_PERSONIDENT_FASTLEGE_AND_VIKAR = PersonIdent("12345678915")
    const val ARBEIDSTAKER_NAME_FIRST = "First"
    const val ARBEIDSTAKER_NAME_MIDDLE = "Middle"
    const val ARBEIDSTAKER_NAME_LAST = "Last"

    val FASTLEGE_FNR = PersonIdent("12125678911")

    const val VEILEDER_IDENT = "Z999999"

    const val JWT_AZP = "syfomodiaperson"
}
