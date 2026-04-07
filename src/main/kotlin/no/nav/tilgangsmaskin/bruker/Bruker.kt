package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.bruker.Familie.Companion.INGEN
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
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

    @Generated
    override fun toString()  = "${javaClass.simpleName}(brukerIds=$brukerIds, geografiskTilknytning=$geografiskTilknytning, påkrevdeGrupper=$påkrevdeGrupper, dødsdato=$dødsdato, foreldreOgBarn=$foreldreOgBarn, barn=$barn, søsken=$søsken, partnere=$partnere, harUkjentBosted=$harUkjentBosted, harUtenlandskBosted=$harUtenlandskBosted)"


    data class BrukerIds(val aktivBrukerId: BrukerId,
                         val oppslagId: String = aktivBrukerId.verdi,
                         val historiskeIds: Set<BrukerId> = emptySet(),
                         val aktørId: AktørId) {
        @Generated
        override fun toString() = "${javaClass.simpleName}(aktivBrukerId=$aktivBrukerId, oppslagId='${oppslagId.maskFnr()}', historiskeIds=$historiskeIds, aktørId=$aktørId)"
    }
}
