package no.nav.tilgangsmaskin.bruker

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.bruker.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
@Timed
class BrukerTjeneste(private val personTjeneste: PDLTjeneste, val skjermingTjeneste: SkjermingTjeneste) {

    private val log = getLogger(javaClass)


    fun brukere(vararg brukerIds: String) : List<Bruker> {
        val personer =  personTjeneste.personer(brukerIds.toSet())
        val notFound = brukerIds.toSet() - personer.map { it.brukerId.verdi }.toSet()
        val found =  personer.map { it.brukerId }.toSet()
        log.info("Bulk fant ikke følgende ${notFound.size} personer  ${notFound.joinToString { it.maskFnr() }}")
        log.info("Bulk fant følgende ${found.size} personer  ${found.joinToString { it.verdi.maskFnr() }}")

        return found.let { p ->
            log.info("Bulk henter skjerminger for $found")
                val skjerminger = skjermingTjeneste.skjerminger(found)
                log.info("Bulk hentet ${skjerminger.size} skjerminger $skjerminger")
                personer.map {
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
