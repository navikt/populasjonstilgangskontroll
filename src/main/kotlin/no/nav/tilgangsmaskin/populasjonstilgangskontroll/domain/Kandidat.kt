package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe

class Kandidat(val ident: Fødselsnummer, private val beskyttelse: GlobalGruppe? = null) {

    fun  kreverGruppe(gruppe: GlobalGruppe) = gruppe == beskyttelse
    override fun toString() = "${javaClass.simpleName} [ident=$ident,beskyttelse=$beskyttelse]"
}