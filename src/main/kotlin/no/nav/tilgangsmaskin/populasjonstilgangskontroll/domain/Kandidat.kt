package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe

class Kandidat(val ident: Fødselsnummer, vararg private val beskyttelse: GlobalGruppe) {

    fun  kreverGruppe(gruppe: GlobalGruppe) = gruppe in beskyttelse
    override fun toString() = "${javaClass.simpleName} [ident=$ident, beskyttelse=$beskyttelse]"
}