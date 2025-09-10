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
import kotlin.text.get

@Service
class BrukerTjeneste(private val personTjeneste: PDLTjeneste, val skjermingTjeneste: SkjermingTjeneste) {

    private val log = getLogger(javaClass)

    @WithSpan
    fun brukere(brukerIds: Set<String>): Set<Bruker> {
        if (brukerIds.isEmpty()) {
            log.debug("Ingen brukere å slå opp")
            return emptySet()
        }
        val personer = personTjeneste.personer(brukerIds)
        log.debug("Bulk brukere slo opp {} av {} personer i PDL ({})", personer.size, brukerIds.size, personer)
        if (personer.isEmpty()) {
            log.debug("Bulk skjerming ingenting å slå opp")
            return emptySet()
        }
        val ids = personer.map { it.brukerId }.toSet()
        log.trace("Bulk skjerming slår opp ${ids.size} skjerminger")
        val skjerminger = skjermingTjeneste.skjerminger(ids)
        log.trace("Bulk skjerming slo opp ${skjerminger.size} skjerminger")
        return personer.map { tilBruker(it, skjerminger[it.brukerId] ?: false) }.toSet()
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
