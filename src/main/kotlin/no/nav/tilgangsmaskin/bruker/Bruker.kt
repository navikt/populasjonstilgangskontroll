package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.sikkerhetstjenesten.felles.NoCoverageAnalysis
import no.nav.sikkerhetstjenesten.felles.domain.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.ansatt.graph.EntraGlobalGruppe
import no.nav.tilgangsmaskin.bruker.Familie.Companion.INGEN_FAMILIE
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UkjentBosted
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.UtenlandskTilknytning
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.Period

data class Bruker(
    val brukerIds: BrukerIds,
    val geografiskTilknytning: GeografiskTilknytning,
    val påkrevdeGrupper: Set<EntraGlobalGruppe> = emptySet(),
    val familie: Familie = INGEN_FAMILIE,
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
    infix fun kreverMedlemskapI(gruppe: EntraGlobalGruppe) = gruppe in påkrevdeGrupper

    infix fun harVærtDødMerEnn(dur: Period) = dødsdato != null && dødsdato.plus(dur) < now()

    @NoCoverageAnalysis
    override fun toString() =
        "${javaClass.simpleName}(brukerIds=$brukerIds, geografiskTilknytning=$geografiskTilknytning, påkrevdeGrupper=$påkrevdeGrupper, dødsdato=$dødsdato, foreldreOgBarn=$foreldreOgBarn, barn=$barn, søsken=$søsken, partnere=$partnere, harUkjentBosted=$harUkjentBosted, harUtenlandskBosted=$harUtenlandskBosted)"


    data class BrukerIds(val aktivBrukerId: BrukerId,
                         val oppslagId: String = aktivBrukerId.verdi,
                         val historiskeIds: Set<BrukerId> = emptySet(),
                         val aktørId: AktørId) {
        @NoCoverageAnalysis
        override fun toString() =
            "${javaClass.simpleName}(aktivBrukerId=$aktivBrukerId, oppslagId='${oppslagId.maskFnr()}', historiskeIds=$historiskeIds, aktørId=$aktørId)"
    }
}
