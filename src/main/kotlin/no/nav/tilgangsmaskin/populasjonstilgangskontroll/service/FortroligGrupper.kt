package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

enum class FortroligeGrupper(val gruppeNavn: String) {
    STRENGT_FORTROLIG("GA_STRENGT_FORTROLIG_ADRESSE"),
    STRENGT_FORTROLIG_UTLAND("GA_STRENGT_FORTROLIG_ADRESSE"),
    FORTROLIG("0000-GA-FORTROLIG_ADRESSE")
}
