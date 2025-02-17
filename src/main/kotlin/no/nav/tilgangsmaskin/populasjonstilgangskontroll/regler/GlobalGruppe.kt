package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AvvisningBegrunnelse.*

enum class GlobalGruppe(val begrunnelse: AvvisningBegrunnelse) {
    STRENGT_FORTROLIG_GRUPPE(AVVIST_STRENGT_FORTROLIG_ADRESSE),
    FORTROLIG_GRUPPE(AVVIST_FORTROLIG_ADRESSE),
    EGEN_ANSATT_GRUPPE(AVVIST_SKJERMING),
    UDEFINERT_GEO_GRUPPE(AVVIST_GEOGRAFISK),
    GEO_PERSON_UTLAND_GRUPPE(AVVIST_PERSON_UTLAND),
    GEO_PERSON_UKJENT_GRUPPE(AVVIST_PERSON_UKJENT)

}

enum class AvvisningBegrunnelse(val årsak: String) {
    AVVIST_STRENGT_FORTROLIG_ADRESSE("Mangler tilgang til streng fortrolig adresse"),
    AVVIST_STRENGT_FORTROLIG_UTLAND("Mangler tilgang til streng fortrolig adresse utland"),
    AVVIST_FORTROLIG_ADRESSE("Mangler tilgang til streng fortrolig adresse"),
    AVVIST_SKJERMING("Mangler tilgng til fortrolig adresse"),
    AVVIST_GEOGRAFISK("Mangler tilgang til brukers geografiske adresse"),
    AVVIST_EGNE_DATA("TODO"),
    AVVIST_EGEN_FAMILIE("TODO"),
    AVVIST_VERGE("TODO"),
    AVVIST_PERSON_UTLAND("Mangler tilgang til person i utlandet"),
    AVVIST_PERSON_UKJENT("Mangler tilgang til person uten kjent adresse")

}