package no.nav.tilgangsmaskin.bruker

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.bruker.pdl.PdlTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
class BrukerTjeneste(private val personTjeneste: PdlTjeneste, val skjermingTjeneste: SkjermingTjeneste) {

    private val log = getLogger(javaClass)

    @WithSpan
    fun brukere(brukerIds: Set<String>): Set<Bruker> {
        if (brukerIds.isEmpty()) {
            log.info("Bulk ingen personer å slå opp")
            return emptySet()
        }
        val personer = personTjeneste.personer(brukerIds)
        if (personer.size != brukerIds.size) {
            val mangler = (brukerIds - personer.map { it.brukerId.verdi }.toSet()).map { it.maskFnr() }
            log.warn("Bulk fant ikke ${mangler.size} brukerIds")
        }

        return if (personer.isNotEmpty()) {
            val brukerIds = personer.map { it.brukerId }
            log.trace("Bulk slår opp {} skjerming(er) for {}", brukerIds.size, brukerIds)
            val skjerminger = skjermingTjeneste.skjerminger(brukerIds)
            log.trace("Bulk slo opp {} skjerminger ({}) for {}",
                skjerminger.size, skjerminger,
                brukerIds.joinToString { it.verdi.maskFnr() })
            personer.map { tilBruker(it, skjerminger[it.brukerId] ?: false) }.toSet()
        } else {
            log.debug("Bulk ingen skjerminger å slå opp")
            emptySet()
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
            tilBruker(this, skjermingTjeneste.skjerming(brukerId)).also {
                log.trace("Bruker er {}", it)
            }
        }
}
