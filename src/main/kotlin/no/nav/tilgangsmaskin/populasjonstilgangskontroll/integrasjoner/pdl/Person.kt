package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie.Companion.INGEN
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning
import java.time.LocalDate

data class Person(
    val brukerId: BrukerId,
    val geoTilknytning: GeografiskTilknytning,
    val graderinger: List<Gradering> = emptyList(),
    val familie: Familie = INGEN,
    val dødsdato: LocalDate? = null,
    val historiskeIdentifikatorer: List<BrukerId> = emptyList()) {

    @JsonIgnore
    val foreldre = familie.foreldre
    @JsonIgnore
    val barn = familie.barn
}

enum class Gradering { STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG,UGRADERT}

