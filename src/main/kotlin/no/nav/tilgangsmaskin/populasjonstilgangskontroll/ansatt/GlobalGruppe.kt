package no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AvvisningKode.*

enum class GlobalGruppe(val kode: AvvisningKode) {
    STRENGT_FORTROLIG_GRUPPE(AVVIST_STRENGT_FORTROLIG_ADRESSE),
    FORTROLIG_GRUPPE(AVVIST_FORTROLIG_ADRESSE),
    EGEN_ANSATT_GRUPPE(AVVIST_SKJERMING),
    UDEFINERT_GEO_GRUPPE(AVVIST_GEOGRAFISK),
    GEO_PERSON_UTLAND_GRUPPE(AVVIST_PERSON_UTLAND),
}

enum class AvvisningKode(val årsak: String) {
    AVVIST_STRENGT_FORTROLIG_ADRESSE("Mangler tilgang til strengt fortrolig adresse"),
    AVVIST_STRENGT_FORTROLIG_UTLAND("Mangler tilgang til strengt fortrolig adresse utland"),
    AVVIST_FORTROLIG_ADRESSE("Mangler tilgang til fortrolig adresse"),
    AVVIST_SKJERMING("Mangler tilgang til skjermet person"),
    AVVIST_GEOGRAFISK("Mangler tilgang til brukers geografiske adresse"),
    AVVIST_HABILITET0("Avvist grunnet manglende habilitet 0"),
    AVVIST_HABILITET1("Avvist grunnet manglende habilitet 1"),
    AVVIST_HABILITET2("Avvist grunnet manglende habilitet 2"),
    AVVIST_HABILITET3("Avvist grunnet manglende habilitet 3"),
    AVVIST_AVDØD("Bruker er avdød"),
    AVVIST_PERSON_UTLAND("Mangler tilgang til person i utlandet"),
    AVVIST_PERSON_UKJENT("Mangler tilgang til person uten kjent adresse")

}