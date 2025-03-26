package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie.Companion.INGEN
import java.time.LocalDate

data class Bruker(
    val brukerId: BrukerId,
    val geoTilknytning: GeoTilknytning,
    val gruppeKrav: List<GlobalGruppe> = emptyList(),
    val familie: Familie = INGEN,
    val dødsdato: LocalDate? = null,
    val historiskeIdentifikatorer: List<BrukerId> = emptyList()) {

    @JsonIgnore
    val familieMedlemmer = familie.familieMedlemmer

    fun kreverGlobalGruppe(gruppe: GlobalGruppe) = gruppe in gruppeKrav

    override fun toString() = "${javaClass.simpleName} [ident=$brukerId, geoTilknytning=$geoTilknytning,  gruppeKrav=$gruppeKrav,identifikatorer=$historiskeIdentifikatorer]"
}
