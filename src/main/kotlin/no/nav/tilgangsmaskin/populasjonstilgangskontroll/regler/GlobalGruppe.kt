package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AvvisningBegrunnelse.*

enum class GlobalGruppe(val begrunnelse: AvvisningBegrunnelse) {
    STRENGT_FORTROLIG_GRUPPE(AVVIST_STRENGT_FORTROLIG_ADRESSE),
    FORTROLIG_GRUPPE(AVVIST_FORTROLIG_ADRESSE),
    EGEN_ANSATT_GRUPPE(AVVIST_SKJERMING),
    UDEFINERT_GEO_GRUPPE(AVVIST_GEOGRAFISK),
    GEO_PERSON_UTLAND_GRUPPE(AVVIST_PERSON_UTLAND),
}

enum class AvvisningBegrunnelse(val årsak: String) {
    AVVIST_STRENGT_FORTROLIG_ADRESSE("Mangler tilgang til strengt fortrolig adresse"),
    AVVIST_STRENGT_FORTROLIG_UTLAND("Mangler tilgang til strengt fortrolig adresse utland"),
    AVVIST_FORTROLIG_ADRESSE("Mangler tilgang til fortrolig adresse"),
    AVVIST_SKJERMING("Mangler tilgang til skjermet person"),
    AVVIST_GEOGRAFISK("Mangler tilgang til brukers geografiske adresse"),
    AVVIST_EGNE_DATA("TODO"),
    AVVIST_EGEN_FAMILIE("TODO"),
    AVVIST_VERGE("TODO"),
    AVVIST_PERSON_UTLAND("Mangler tilgang til person i utlandet"),
    AVVIST_PERSON_UKJENT("Mangler tilgang til person uten kjent adresse")

}