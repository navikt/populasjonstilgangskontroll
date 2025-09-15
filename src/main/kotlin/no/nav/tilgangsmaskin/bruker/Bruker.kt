package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.Familie.Companion.INGEN
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import java.time.LocalDate

data class Bruker(
        val brukerIds: BrukerIds,
        val geografiskTilknytning: GeografiskTilknytning,
        val påkrevdeGrupper: Set<GlobalGruppe> = emptySet(),
        val familie: Familie = INGEN,
        val dødsdato: LocalDate? = null) {

    @JsonIgnore
    val brukerId = brukerIds.aktivBrukerId

    val oppslagId = brukerIds.oppslagId

    @JsonIgnore
    val aktørId = brukerIds.aktørId

    @JsonIgnore
    val historiskeIds = brukerIds.historiskeIds

    @JsonIgnore
    val foreldreOgBarn = familie.foreldre + familie.barn

    @JsonIgnore
    val barn = familie.barn

    @JsonIgnore
    val søsken = familie.søsken

    @JsonIgnore
    val partnere = familie.partnere

    val harUkjentBosted = geografiskTilknytning is UkjentBosted
    val harUtenlandskBosted = geografiskTilknytning is UtenlandskTilknytning
    infix fun kreverMedlemskapI(gruppe: GlobalGruppe) = gruppe in påkrevdeGrupper

    data class BrukerIds(val aktivBrukerId: BrukerId,
                         val oppslagId: String,
                         val historiskeIds: Set<BrukerId> = emptySet(),
                         val aktørId: AktørId? = null)

}
