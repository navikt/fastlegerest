package no.nav.syfo.testhelper

import no.nav.syfo.util.PersonIdent

object UserConstants {
    val ARBEIDSTAKER_PERSONIDENT = PersonIdent("12345678912")
    val ARBEIDSTAKER_PERSONIDENT_VEILEDER_NO_ACCESS = PersonIdent("12345678913")
    val ARBEIDSTAKER_PERSONIDENT_NO_FASTLEGE = PersonIdent("12345678914")
    const val ARBEIDSTAKER_NAME_FIRST = "First"
    const val ARBEIDSTAKER_NAME_MIDDLE = "Middle"
    const val ARBEIDSTAKER_NAME_LAST = "Last"

    const val FASTLEGEOPPSLAG_PERSON_ID = "10101012345"
    const val FASTLEGEOPPSLAG_PERSON_ID_MISSING_PST_ADR = "11101012345"
    const val FASTLEGEOPPSLAG_PERSON_ID_MISSING_RES_ADR = "12101012345"
    const val FASTLEGEOPPSLAG_PERSON_ID_MISSING_HER_ID = "13101012345"
    const val FASTLEGEOPPSLAG_PERSON_ID_MISSING_HPR_NR = "14101012345"
    const val FASTLEGEOPPSLAG_PERSON_ID_MISSING_NIN = "15101012345"
    const val FASTLEGE_FORNAVN = "Willy"
    const val FASTLEGE_ETTERNAVN = "Lege etternavn"
    const val FASTLEGE_FNR = "20202012345"
    const val FASTLEGE_RELASJON_KODEVERDI = "LPFL"
    const val FASTLEGE_RELASJON_KODETEKST = "Fastlege"
    const val FASTLEGE_STILLINGSPROSENT = 100

    const val FASTLEGE_VIKAR_FORNAVN = "Vikar"
    const val FASTLEGE_VIKAR_ETTERNAVN = "Legevikar etternavn"
    const val FASTLEGE_VIKAR_FNR = "10202012345"
    const val FASTLEGE_VIKAR_RELASJON_KODEVERDI = "LPVI"
    const val FASTLEGE_VIKAR_RELASJON_KODETEKST = "Vikar"
    const val FASTLEGE_VIKAR_HPR_NR = 23456
    const val FASTLEGE_VIKAR_STILLINGSPROSENT = 60

    const val FASTLEGEKONTOR_NAVN = "Fastlegekontoret"
    const val FASTLEGEKONTOR_ORGNR = 123456789
    const val FASTLEGEKONTOR_TLF = "12345678"
    const val FASTLEGEKONTOR_EPOST = "test@nav.no"
    const val FASTLEGEKONTOR_POSTSTED = "Oslo"
    const val FASTLEGEKONTOR_POSTNR = 651
    const val FASTLEGEKONTOR_ADR = "Storgata 2"
    const val FASTLEGEKONTOR_POSTBOKS = "Boks 99"

    const val FASTLEGE_HPR_NR = 12345

    const val VEILEDER_IDENT = "Z999999"
    const val HER_ID = 1234
    const val PARENT_HER_ID = 9876

    const val JWT_AZP = "syfomodiaperson"
}
