package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.Familie.Companion.INGEN

data class Bruker(
        val brukerIdentifikatorer: BrukerIdentifikatorer,
        val geografiskTilknytning: GeografiskTilknytning,
        val gruppeKrav: List<GlobalGruppe> = emptyList(),
        val familie: Familie = INGEN,
        val dødsdato: LocalDate? = null) {

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

    inline fun <reified T : GeografiskTilknytning> erRegistrertMed() = geografiskTilknytning is T

    infix fun krever(gruppe: GlobalGruppe) = gruppe in gruppeKrav

    data class BrukerIdentifikatorer(
            val brukerId: BrukerId,
            val aktørId: AktørId,
            val historiskeIdentifikatorer: List<BrukerId> = emptyList()
                                    )


}
