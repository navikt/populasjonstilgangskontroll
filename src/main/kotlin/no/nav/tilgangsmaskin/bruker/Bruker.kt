package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.Familie.Companion.INGEN
import java.time.LocalDate

data class Bruker(
    val brukerIdentifikatorer: BrukerIdentifikatorer,
    val geografiskTilknytning: GeografiskTilknytning,
    val gruppeKrav: List<GlobalGruppe> = emptyList(),
    val familie: Familie = INGEN,
    val dødsdato: LocalDate? = null
) {

    @JsonIgnore
    val brukerId = brukerIdentifikatorer.brukerId

    @JsonIgnore
    val historiskeIdentifikatorer = brukerIdentifikatorer.historiskeIdentifikatorer

    @JsonIgnore
    val foreldreOgBarn = familie.foreldre + familie.barn

    @JsonIgnore
    val erDød = dødsdato != null

    @JsonIgnore
    val søsken = familie.søsken

    @JsonIgnore
    val partnere = familie.partnere

    fun kreverGlobalGruppe(gruppe: GlobalGruppe) = gruppe in gruppeKrav

    data class BrukerIdentifikatorer(
        val brukerId: BrukerId,
        val aktørId: AktørId,
        val historiskeIdentifikatorer: List<BrukerId> = emptyList()
    )


}
