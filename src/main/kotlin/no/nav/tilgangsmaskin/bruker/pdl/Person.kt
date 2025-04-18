package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.Familie.Companion.INGEN
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import java.time.LocalDate

data class Person(
    val brukerId: BrukerId,
    val aktørId: AktørId,
    val geoTilknytning: GeografiskTilknytning,
    val graderinger: List<Gradering> = emptyList(),
    val familie: Familie = INGEN,
    val dødsdato: LocalDate? = null,
    val historiskeIdentifikatorer: List<BrukerId> = emptyList()
) {

    @JsonIgnore
    val foreldre = familie.foreldre

    @JsonIgnore
    val barn = familie.barn
}

enum class Gradering { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG, UGRADERT }

fun List<Gradering>.erStrengtFortrolig() =
    any { it in setOf(Gradering.STRENGT_FORTROLIG_UTLAND, Gradering.STRENGT_FORTROLIG) }

fun List<Gradering>.erFortrolig() = any { it == Gradering.FORTROLIG }
