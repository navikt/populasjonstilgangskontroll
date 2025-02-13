package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe

class Bruker(val ident: Fødselsnummer, val navn: Navn, val geoTilknytning: GeoTilknytning, vararg val gruppeKrav: GlobalGruppe) {

    fun  kreverGruppe(gruppe: GlobalGruppe) = gruppe in gruppeKrav

    override fun toString() = "${javaClass.simpleName} [ident=$ident, geoTilknytning=$geoTilknytning,  gruppeKrav=${gruppeKrav.contentToString()}]"
}