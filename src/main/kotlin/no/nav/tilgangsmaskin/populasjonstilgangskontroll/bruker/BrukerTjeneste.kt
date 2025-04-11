package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.springframework.stereotype.Service

@Service
@Timed
class BrukerTjeneste(private val personer: PDLTjeneste, val skjerminger: SkjermingTjeneste) {

    fun brukere(brukerIds: List<BrukerId>) =
        run {
            val skjerminger = skjerminger.skjerminger(brukerIds)
            personer.personer(brukerIds).map {
                tilBruker(it, skjerminger[it.brukerId] ?: false)
            }
        }

    @Deprecated("Bruk streng for å støøte alternative IDer")
    fun bruker(brukerId: BrukerId) =bruker(brukerId.verdi)

    fun bruker(brukerId: String)  =
        run {
            val person = personer.person(brukerId)
            val skjermet = skjerminger.skjerming(person.brukerId)
            tilBruker(person, skjermet)
        }
}
