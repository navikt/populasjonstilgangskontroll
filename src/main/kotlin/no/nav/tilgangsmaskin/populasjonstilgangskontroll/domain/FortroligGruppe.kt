package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

enum class FortroligGruppe(val gruppeNavn: String) {
    STRENGT_FORTROLIG("0000-GA-STRENGT_FORTROLIG_ADRESSE"),
    FORTROLIG("0000-GA-FORTROLIG_ADRESSE"),
    INGEN("Ubeskytte todo")
}
