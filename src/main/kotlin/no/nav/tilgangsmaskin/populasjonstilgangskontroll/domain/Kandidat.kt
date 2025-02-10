package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe

data class Kandidat(val ident: Fødselsnummer,
                    private val beskyttelse: List<GlobalGruppe>) {

    fun  kreverGruppe(gruppe: GlobalGruppe) = beskyttelse.contains(gruppe)
    override fun toString() = "${javaClass.simpleName} [ident=$ident, beskyttelse=$beskyttelse]"
}