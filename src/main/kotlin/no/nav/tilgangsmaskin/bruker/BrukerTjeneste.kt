package no.nav.tilgangsmaskin.bruker

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.bruker.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.Person
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
@Timed
class BrukerTjeneste(private val personer: PDLTjeneste, val skjerminger: SkjermingTjeneste) {

    private val log = getLogger(javaClass)


    fun brukere(vararg brukerIds: String) = personer.personer(brukerIds.toSet()).let { personer ->
        val skjerminger = skjerminger.skjerminger(personer.map { it.brukerId }.toSet())
        personer.map {
            tilBruker(it, skjerminger[it.brukerId] ?: false)
        }
    }

    fun brukerMedNærmesteFamilie(brukerId: String) =
        brukerMedSkjerming(brukerId, personer::medNærmesteFamilie)

    fun brukerMedUtvidetFamilie(brukerId: String) =
        brukerMedSkjerming(brukerId, personer::medUtvidetFamile)

    private fun brukerMedSkjerming(id: String, hentFamilie: (String) -> Person) =
        with(hentFamilie(id)) {
            val statuser = skjerminger.skjerminger(historiskeIds + brukerId)
            statuser.filterValues { it }.forEach { (key, _) ->
                if (key.verdi != id) {
                    log.info("Bruker $key er skjermet grunnet historikk")
                }
                else {
                    log.trace("Bruker {} er skjermet", key)
                }
            }
            tilBruker(this, statuser.values.any { it })
        }
}
