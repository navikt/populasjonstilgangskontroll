package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.Familie.Companion.INGEN
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.*
import no.nav.tilgangsmaskin.felles.cache.JsonCacheable
import java.time.LocalDate

@JsonCacheable
data class Person(
    val brukerId: BrukerId,
    val oppslagId: String = brukerId.verdi,
    val aktørId: AktørId,
    val geoTilknytning: GeografiskTilknytning,
    val graderinger: List<Gradering> = emptyList(),
    val familie: Familie = INGEN,
    val dødsdato: LocalDate? = null,
    val historiskeIds: Set<BrukerId> = emptySet())  {

    @JsonIgnore
    val foreldre = familie.foreldre

    @JsonIgnore
    val barn = familie.barn

    enum class Gradering { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG, UGRADERT }

}

fun List<Gradering>.erStrengtFortroligUtland() = inneholder(STRENGT_FORTROLIG_UTLAND)

fun List<Gradering>.erStrengtFortrolig() = inneholder(STRENGT_FORTROLIG)

fun List<Gradering>.erFortrolig() = inneholder(FORTROLIG)

private fun List<Gradering>.inneholder(gradering: Gradering) = any { it == gradering }

