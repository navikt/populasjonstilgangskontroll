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
class BrukerTjeneste(private val personTjeneste: PDLTjeneste, val skjermingTjeneste: SkjermingTjeneste) {

    private val log = getLogger(javaClass)


    fun brukere(vararg brukerIds: String) : List<Bruker> {
        val personer =  personTjeneste.personer(brukerIds.toSet())
        val notFound = brukerIds.toSet() - personer.map { it.brukerId.verdi }.toSet()
        val found =  personer.filter { !notFound.contains(it.brukerId.verdi) }
        log.info("Bulk Fant ikke personer for ${notFound.joinToString(",")}")
        log.info("Bulk Fant personer for ${found.joinToString(",")}")

        return found.let { p ->
            log.info("Bulk hentet ${p.size} brukere: for ${brukerIds.joinToString(",")}")
            val skjerminger = skjermingTjeneste.skjerminger(p.map { it.brukerId }.toSet())
            p.map {
                tilBruker(it, skjerminger[it.brukerId] ?: false)
            }
        }
    }

    fun brukerMedNærmesteFamilie(brukerId: String) =
        brukerMedSkjerming(brukerId, personTjeneste::medNærmesteFamilie)

    fun brukerMedUtvidetFamilie(brukerId: String) =
        brukerMedSkjerming(brukerId, personTjeneste::medUtvidetFamile)

    private fun brukerMedSkjerming(id: String, hentFamilie: (String) -> Person) =
        with(hentFamilie(id)) {
            tilBruker(this, skjermingTjeneste.skjerming(brukerId))
        }
            /*
            val statuser = skjermingTjeneste.skjerminger(historiskeIds + brukerId)
            statuser.filterValues { it }.forEach { (brukerId, _) ->
                if (brukerId.verdi != id) {
                    log.info("Bruker $brukerId er skjermet grunnet historikk")
                }
            }
            tilBruker(this, skjermingTjeneste.skjerming(this.brukerId))
        }*/
}
