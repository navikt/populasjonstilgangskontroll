package no.nav.tilgangsmaskin.bruker

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.bruker.pdl.PDLTjeneste
import org.springframework.stereotype.Service

@Service
@Timed
class BrukerTjeneste(private val personer: PDLTjeneste, val skjerminger: SkjermingTjeneste) {

    fun brukere(vararg brukerIds: String) = personer.personer(brukerIds.toSet()).let { personer ->
        val skjerminger = skjerminger.skjerminger(personer.map { it.brukerId }.toSet())
        personer.map {
            tilBruker(it, skjerminger[it.brukerId] ?: false)
        }
    }

    fun medNærmesteFamilie(brukerId: String) =
        personer.medNærmesteFamilie(brukerId).let {
            tilBruker(it, skjerminger.skjerming(it.brukerId))
        }

    fun medUtvidetFamilie(brukerId: String) =
        personer.medUtvidetFamile(brukerId).let {
            tilBruker(it, skjerminger.skjerming(it.brukerId))
        }
}
