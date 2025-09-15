package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.bruker.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.Person
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import io.opentelemetry.instrumentation.annotations.WithSpan

@Service
class BrukerTjeneste(private val personTjeneste: PDLTjeneste, val skjermingTjeneste: SkjermingTjeneste) {

    private val log = getLogger(javaClass)

    @WithSpan
    fun brukere(brukerIds: Set<String>) : Set<Bruker> {
        if (brukerIds.isEmpty()) {
            log.info("Bulk ingen personer å slå opp")
            return emptySet()
        }
        val personer =  personTjeneste.personer(brukerIds)
        val notFound = brukerIds - personer.map { it.aktivBrukerId.verdi }.toSet()
        val found =  personer.map { it.aktivBrukerId }.toSet()
        if (notFound.isNotEmpty()) {
            log.warn("Bulk fant ikke ${"person".pluralize(notFound)}: ${notFound.joinToString { it.maskFnr() }}")
        }
        if (found.isNotEmpty()) {
            log.info("Bulk slo opp ${found.size} person(er) av totalt ${brukerIds.size}")
        }

        return found.let { p ->
            if (p.isNotEmpty()) {
                log.trace("Bulk slår opp {} for {}", "skjerming".pluralize(p), p)
                val skjerminger = skjermingTjeneste.skjerminger(p)
                log.trace("Bulk slo opp ${"skjerming".pluralize(skjerminger.keys)} for ${p.joinToString { it.verdi.maskFnr() }}")
                personer.map {
                    tilBruker(it, skjerminger[it.aktivBrukerId] ?: false)
                }
            } else {
                log.debug("Bulk ${"skjerming".pluralize(p, ingen = "Ingen")} å slå opp")
                emptyList()
            }.toSet()
        }
    }

    @WithSpan
    fun brukerMedNærmesteFamilie(brukerId: String) =
        brukerMedSkjerming(brukerId, personTjeneste::medFamilie)

    @WithSpan
    fun brukerMedUtvidetFamilie(brukerId: String) =
        brukerMedSkjerming(brukerId, personTjeneste::medUtvidetFamile)

    @WithSpan
    private fun brukerMedSkjerming(id: String, hentFamilie: (String) -> Person) =
        with(hentFamilie(id)) {
            tilBruker(this, skjermingTjeneste.skjerming(aktivBrukerId))
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
