package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.Familie.Companion.INGEN
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning

data class Bruker(
        val brukerIdentifikatorer: BrukerIdentifikatorer,
        val geografiskTilknytning: GeografiskTilknytning,
        val gruppeKrav: Set<GlobalGruppe> = emptySet(),
        val familie: Familie = INGEN,
        val dødsdato: LocalDate? = null) {

    @JsonIgnore
    val brukerId = brukerIdentifikatorer.brukerId

    @JsonIgnore
    val historiskeIdentifikatorer = brukerIdentifikatorer.historiskeIdentifikatorer

    @JsonIgnore
    val foreldreOgBarn = familie.foreldre + familie.barn
    
    @JsonIgnore
    val søsken = familie.søsken

    @JsonIgnore
    val partnere = familie.partnere

    private inline fun <reified T : GeografiskTilknytning> erRegistrertMed() = geografiskTilknytning is T

    val harUkjentBosted = erRegistrertMed<UkjentBosted>()
    val harUtenlandskBosted = erRegistrertMed<UtenlandskTilknytning>()
    infix fun krever(gruppe: GlobalGruppe) = gruppe in gruppeKrav

    data class BrukerIdentifikatorer(
            val brukerId: BrukerId,
            val aktørId: AktørId,
            val historiskeIdentifikatorer: List<BrukerId> = emptyList())
}
