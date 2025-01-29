package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

@JvmInline
value class NavId(val verdi: String) {
    init {
        require(verdi.length == 7) { "Ugyldig lengde på ident: $verdi" }
        require(verdi.first().isUpperCase()) { "Ugyldig første tegn i ident: $verdi, må være stor bokstav" }
    }
}