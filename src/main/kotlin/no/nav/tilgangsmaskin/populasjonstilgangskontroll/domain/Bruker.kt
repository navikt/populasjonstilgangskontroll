package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe

class Bruker(val ident: Fødselsnummer, val navn: Navn, val tilknytning: GEOTilknytning, vararg val gruppeKrav: GlobalGruppe) {

    fun  kreverGruppe(gruppe: GlobalGruppe) = gruppe in gruppeKrav
    override fun toString() = "${javaClass.simpleName} [ident=$ident, tilknytning=$tilknytning,  gruppeKrav=${gruppeKrav.contentToString()}]"
}