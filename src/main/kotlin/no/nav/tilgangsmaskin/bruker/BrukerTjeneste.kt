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
        log.info("Bulk kall for $brukerIds")
        if (brukerIds.isEmpty()) {
            log.info("Bulk ingen personer å slå opp")
            return emptySet()
        }
        val personer =  personTjeneste.personer(brukerIds)
        log.info("Bulk slo opp følgende ${personer.size} $personer fra ${brukerIds.size} $brukerIds i PDL")
        val funnetBrukerIds = buildList {
            personer.forEach { add(brukerIdForOppslagId(it.oppslagId, personer)) }
        }
        log.info("Bulk funner ${funnetBrukerIds.size} brukerIds $funnetBrukerIds")
        if (funnetBrukerIds.size != personer.size) {
            val mangler = (brukerIds - funnetBrukerIds.map { it.verdi }.toSet()).map { it.maskFnr() }
            log.warn("Bulk fant ikke $mangler")
        }

        return funnetBrukerIds.let { p ->
            if (p.isNotEmpty()) {
                log.trace("Bulk slår opp {} for {}", "skjerming".pluralize(p), p)
                val skjerminger = skjermingTjeneste.skjerminger(p)
                log.trace("Bulk slo opp ${"skjerming".pluralize(skjerminger.keys)} for ${p.joinToString { it.verdi.maskFnr() }}")
                personer.map {
                    tilBruker(it, skjerminger[it.brukerId] ?: false)
                }
            } else {
                log.debug("Bulk ${"skjerming".pluralize(p, ingen = "Ingen")} å slå opp")
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
