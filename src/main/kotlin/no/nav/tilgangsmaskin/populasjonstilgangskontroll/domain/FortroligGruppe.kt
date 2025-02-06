package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

enum class FortroligGruppe(val gruppeNavn: String) {
    STRENGT_FORTROLIG("GA_STRENGT_FORTROLIG_ADRESSE"),
    FORTROLIG("0000-GA-FORTROLIG_ADRESSE")
}
