package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

class Kandidat(val ident: Fødselsnummer, private val beskyttelse: FortroligGruppe?) {

    fun  krevergGruppe(gruppe: FortroligGruppe) = gruppe == beskyttelse

}