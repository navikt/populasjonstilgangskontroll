package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe

class Bruker(val brukerId: BrukerId, val navn: Navn, val geoTilknytning: GeoTilknytning, vararg val gruppeKrav: GlobalGruppe) {

    fun  kreverGlobalGruppe(gruppe: GlobalGruppe) = gruppe in gruppeKrav

    override fun toString() = "${javaClass.simpleName} [ident=$brukerId, geoTilknytning=$geoTilknytning,  gruppeKrav=${gruppeKrav.contentToString()}]"
}