package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Familie.Companion.INGEN
import java.time.LocalDate

class Bruker(
    val brukerId: BrukerId,
    val geoTilknytning: GeoTilknytning,
    val gruppeKrav: List<GlobalGruppe> = emptyList(),
    familie: Familie = INGEN,
    val dødsdato: LocalDate? = null,
    val historiskeIdentifikatorer: List<BrukerId> = emptyList()) {

    @JsonIgnore
    val familieMedlemmer = familie.familieMedlemmer

    fun kreverGlobalGruppe(gruppe: GlobalGruppe) = gruppe in gruppeKrav

    override fun toString() = "${javaClass.simpleName} [ident=$brukerId, geoTilknytning=$geoTilknytning,  gruppeKrav=$gruppeKrav,identifikatorer=$historiskeIdentifikatorer]"
}

