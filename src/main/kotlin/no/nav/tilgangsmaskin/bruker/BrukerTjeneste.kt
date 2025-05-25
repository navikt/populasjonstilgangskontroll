package no.nav.tilgangsmaskin.bruker

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.bruker.pdl.PDLTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
@Timed
class BrukerTjeneste(private val personer: PDLTjeneste, val skjerminger: SkjermingTjeneste) {

    private val log = getLogger(javaClass)

    fun brukere(vararg brukerIds: String) = personer.personer(brukerIds.toSet()).let { personer ->
        val brukerIdSet = personer.map { it.brukerId }.toSet()
        log.info("Henter skjerminger for $brukerIdSet")
        val skjermingerMap = skjerminger.skjerminger(brukerIdSet).also {
            log.info("Hentet skjerminger $it for  brukere: $brukerIdSet")
        }
        personer.map { tilBruker(it, skjermingerMap[it.brukerId.verdi] ?: false) }
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
