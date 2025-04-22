package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.Familie.Companion.INGEN
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning

data class Bruker(
        val brukerIdentifikatorer: BrukerIds,
        val geografiskTilknytning: GeografiskTilknytning,
        val gruppeKrav: Set<GlobalGruppe> = emptySet(),
        val familie: Familie = INGEN,
        val dødsdato: LocalDate? = null) {

    @JsonIgnore
    val brukerId = brukerIdentifikatorer.brukerId

    @JsonIgnore
    val historiskeIds = brukerIdentifikatorer.historiskeIds

    @JsonIgnore
    val foreldreOgBarn = familie.foreldre + familie.barn

    @JsonIgnore
    val søsken = familie.søsken

    @JsonIgnore
    val partnere = familie.partnere

    val harUkjentBosted = geografiskTilknytning is UkjentBosted
    val harUtenlandskBosted = geografiskTilknytning is UtenlandskTilknytning
    infix fun krever(gruppe: GlobalGruppe) = gruppe in gruppeKrav

    data class BrukerIds(val brukerId: BrukerId, val aktørId: AktørId, val historiskeIds: Set<BrukerId> = emptySet())
}
