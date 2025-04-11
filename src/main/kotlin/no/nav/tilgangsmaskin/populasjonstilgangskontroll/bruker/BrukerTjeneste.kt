package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.springframework.stereotype.Service

@Service
@Timed
class BrukerTjeneste(private val personer: PDLTjeneste, val skjerminger: SkjermingTjeneste) {

    fun brukere(brukerIds: Set<String>) = personer.personer(brukerIds).let { personer ->
        val skjerminger = skjerminger.skjerminger(personer.map { it.brukerId })
        personer.map { person ->
            tilBruker(person, skjerminger[person.brukerId] ?: false)
        }
    }

    fun bruker(brukerId: String) =
        personer.person(brukerId).let {
            tilBruker(it, skjerminger.skjerming(it.brukerId))
        }
}
