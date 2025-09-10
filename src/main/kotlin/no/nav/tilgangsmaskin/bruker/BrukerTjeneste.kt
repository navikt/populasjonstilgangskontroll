package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.bruker.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.pluralize
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import io.opentelemetry.instrumentation.annotations.WithSpan

@Service
class BrukerTjeneste(private val personTjeneste: PDLTjeneste, val skjermingTjeneste: SkjermingTjeneste) {

    private val log = getLogger(javaClass)

    @WithSpan
    fun brukere(brukerIds: Set<String>) : Set<Bruker> {
        if (brukerIds.isEmpty()) {
            log.debug("Ingen brukere å slå opp")
            return emptySet()
        }
        log.debug("Bulk brukere slår opp $brukerIds")
        val personer =  personTjeneste.personer(brukerIds)
        log.debug("Bulk brukere slo opp {} av {} personer i PDL ({})", personer.size,brukerIds.size, personer)
        val notFound = brukerIds - (personer.map { it.brukerId.verdi }.toSet())
        log.debug("Bulk ikke funnet {}", notFound)
        val found =  personer.map { it.brukerId }.toSet()
        if (notFound.isNotEmpty()) {
            log.warn("Bulk brukere fant ikke $notFound")
        }
        if (found.isNotEmpty()) {
            log.trace("Bulk brukere slo opp {}", found)
        }

        return found.let { p ->
            if (p.isNotEmpty()) {
                log.trace("Bulk skjerming slår opp {} for {}", "skjerming".pluralize(p), p)
                val skjerminger = skjermingTjeneste.skjerminger(p)
                log.trace("Bulk skjerming slo opp ${"skjerming".pluralize(skjerminger.keys)} for ${p.joinToString { it.verdi.maskFnr() }}")
                personer.map {
                    tilBruker(it, skjerminger[it.brukerId] ?: false)
                }
            } else {
                log.debug("Bulk skjerming ingen å slå opp")
                emptyList()
            }.toSet()
        }
    }

    @WithSpan
    fun brukerMedNærmesteFamilie(brukerId: String) =
        brukerMedSkjerming(brukerId, personTjeneste::medNærmesteFamilie)

    @WithSpan
    fun brukerMedUtvidetFamilie(brukerId: String) =
        brukerMedSkjerming(brukerId, personTjeneste::medUtvidetFamile)

    @WithSpan
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
