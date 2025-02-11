package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe

class Kandidat(val ident: Fødselsnummer, private vararg val gruppeKrav: GlobalGruppe) {

    fun  kreverGruppe(gruppe: GlobalGruppe) = gruppe in gruppeKrav
    override fun toString() = "${javaClass.simpleName} [ident=$ident, gruppeKrav=$gruppeKrav]"
}