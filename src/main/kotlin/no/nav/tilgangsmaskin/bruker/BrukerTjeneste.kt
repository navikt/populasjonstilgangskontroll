package no.nav.tilgangsmaskin.bruker

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.bruker.pdl.PDLTjeneste
import org.springframework.stereotype.Service

@Service
@Timed
class BrukerTjeneste(private val personer: PDLTjeneste, val skjerminger: SkjermingTjeneste) {

    fun brukere(brukerIds: Set<String>) = personer.personer(brukerIds).let { personer ->
        val skjerminger = skjerminger.skjerminger(personer.map { it.brukerId })
        personer.map {
            tilBruker(it, skjerminger[it.brukerId] ?: false)
        }
    }

    fun nærmesteFamilie(brukerId: String) =
        personer.nærmesteFamilie(brukerId).let {
            tilBruker(it, skjerminger.skjerming(it.brukerId))
        }

    fun utvidetFamilie(brukerId: String) =
        personer.utvidetFamile(brukerId).let {
            tilBruker(it, skjerminger.skjerming(it.brukerId))
        }
}
