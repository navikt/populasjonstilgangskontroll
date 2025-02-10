package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe

class Kandidat(val ident: Fødselsnummer, private vararg val beskyttelser: GlobalGruppe) {

    fun  kreverGruppe(gruppe: GlobalGruppe) = gruppe in beskyttelser
    override fun toString() = "${javaClass.simpleName} [ident=$ident, beskyttelser=$beskyttelser]"
}