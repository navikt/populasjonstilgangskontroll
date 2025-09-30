package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.bruker.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.Person
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.pluralize
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import io.opentelemetry.instrumentation.annotations.WithSpan

@Service
class BrukerTjeneste(private val personTjeneste: PDLTjeneste, val skjermingTjeneste: SkjermingTjeneste) {

    private val log = getLogger(javaClass)

    @WithSpan
    fun brukere(brukerIds: Set<String>) : Set<Bruker> {
        log.info("Bulk kall for ${brukerIds.size} brukerId(s)")
        if (brukerIds.isEmpty()) {
            log.info("Bulk ingen personer å slå opp")
            return emptySet()
        }
        val personer =  personTjeneste.personer(brukerIds)
        log.info("Bulk slo opp ${personer.size} $personer fra ${brukerIds.size} $brukerIds i PDL")
        val funnetBrukerIds = buildList {
            personer.forEach { add(brukerIdForOppslagId(it.oppslagId, personer)) }
        }
        if (funnetBrukerIds.size != personer.size) {
            val mangler = (brukerIds - funnetBrukerIds.map { it.verdi }.toSet()).map { it.maskFnr() }
            log.warn("Bulk fant ikke $mangler brukerIds")
        }

        return funnetBrukerIds.let { p ->
            if (p.isNotEmpty()) {
                log.trace("Bulk slår opp {} skjerming(er) for {}", p.size,p)
                val skjerminger = skjermingTjeneste.skjerminger(p)
                log.trace("Bulk slo opp {} skjerminger  ($skjerminger) for {}", skjerminger.size, p.joinToString { it.verdi.maskFnr() })
                personer.map {
                    tilBruker(it, skjerminger[it.brukerId] ?: false)
                }
            } else {
                log.debug("Bulk ingen skjerminger å slå opp")
                emptyList()
            }.toSet()
        }
    }

    private fun brukerIdForOppslagId(oppslagId: String, personer: Set<Person>) = personer.first { it.oppslagId == oppslagId }.brukerId

    @WithSpan
    fun brukerMedNærmesteFamilie(brukerId: String) =
        brukerMedSkjerming(brukerId, personTjeneste::medFamilie)

    @WithSpan
    fun brukerMedUtvidetFamilie(brukerId: String) =
        brukerMedSkjerming(brukerId, personTjeneste::medUtvidetFamile)

    @WithSpan
    private fun brukerMedSkjerming(id: String, hentFamilie: (String) -> Person) =
        with(hentFamilie(id)) {
            tilBruker(this, skjermingTjeneste.skjerming(brukerId)).also {
                log.trace("Bruker er {}", it)
            }
        }
}
