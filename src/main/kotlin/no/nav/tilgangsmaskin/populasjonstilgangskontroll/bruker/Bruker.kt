package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie.Companion.INGEN
import java.time.LocalDate

data class Bruker(
    val brukerId: BrukerId,
    val geografiskTilknytning: GeografiskTilknytning,
    val gruppeKrav: List<GlobalGruppe> = emptyList(),
    val familie: Familie = INGEN,
    val dødsdato: LocalDate? = null,
    val historiskeIdentifikatorer: List<BrukerId> = emptyList()) {

    @JsonIgnore
    val familieMedlemmer = familie.familieMedlemmer

    @JsonIgnore
    val foreldreOgBarn = familie.foreldre + familie.barn

    val erDød  = dødsdato != null

    @JsonIgnore
    val søsken = familie.søsken

    fun kreverGlobalGruppe(gruppe: GlobalGruppe) = gruppe in gruppeKrav

}
