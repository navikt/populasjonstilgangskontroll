package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Familie.Companion.INGEN
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe

class Bruker(val brukerId: BrukerId,
             val geoTilknytning: GeoTilknytning,
             val familie: Familie = INGEN,
             vararg val gruppeKrav: GlobalGruppe) {

    fun  kreverGlobalGruppe(gruppe: GlobalGruppe) = gruppe in gruppeKrav


    override fun toString() = "${javaClass.simpleName} [ident=$brukerId, geoTilknytning=$geoTilknytning,  gruppeKrav=${gruppeKrav.contentToString()}]"
}