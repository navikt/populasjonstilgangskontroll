package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie.Companion.INGEN
import java.time.LocalDate
import java.time.LocalDateTime

data class Bruker(
    val brukerIdentifikatorer: BrukerIdentifikatorer,
    val geografiskTilknytning: GeografiskTilknytning,
    val gruppeKrav: List<GlobalGruppe> = emptyList(),
    val familie: Familie = INGEN,
    val dødsdato: LocalDate? = null) {

    @JsonIgnore
    val brukerId = brukerIdentifikatorer.brukerId

    @JsonIgnore
    val historiskeIdentifikatorer = brukerIdentifikatorer.historiskeIdentifikatorer

    @JsonIgnore
    val foreldreOgBarn = familie.foreldre + familie.barn

    @JsonIgnore
    val erDød  = dødsdato != null

    @JsonIgnore
    val søsken = familie.søsken

    fun kreverGlobalGruppe(gruppe: GlobalGruppe) = gruppe in gruppeKrav

    data class BrukerIdentifikatorer(val brukerId: BrukerId, val aktørId: AktørId,  val historiskeIdentifikatorer: List<BrukerId> = emptyList())


}
