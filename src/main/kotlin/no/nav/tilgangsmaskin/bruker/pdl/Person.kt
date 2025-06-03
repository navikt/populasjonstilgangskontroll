package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.Familie.Companion.INGEN
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.STRENGT_FORTROLIG_UTLAND
import java.io.Serializable

data class Person(
        val brukerId: BrukerId,
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

fun List<Gradering>.erStrengtFortroligUtland() = any { it == STRENGT_FORTROLIG_UTLAND }

fun List<Gradering>.erStrengtFortrolig() = any { it == STRENGT_FORTROLIG }

fun List<Gradering>.erFortrolig() = any { it == FORTROLIG }

