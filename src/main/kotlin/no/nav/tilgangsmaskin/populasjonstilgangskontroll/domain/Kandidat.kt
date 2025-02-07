package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

class Kandidat(val ident: Fødselsnummer, private val beskyttelse: FortroligGruppe) {

    fun  kreverGruppe(gruppe: FortroligGruppe) = gruppe == beskyttelse
    val erUbeskyttet = beskyttelse == FortroligGruppe.INGEN

    override fun toString() = "${javaClass.simpleName} [ident=$ident,beskyttelse=$beskyttelse]"
}