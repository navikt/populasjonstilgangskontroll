package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlGeoTilknytning
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPerson
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipTilBrukerMapper
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlTilBrukerMapper
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BrukerTjeneste(private val pdlTjeneste: PDLTjeneste,val egenAnsatt: SkjermingTjeneste) {

    private val log = LoggerFactory.getLogger(BrukerTjeneste::class.java)

    fun brukerGammel(brukerId: BrukerId) =
        run {
            val skjermet = egenAnsatt.erSkjermet(brukerId).also { log.info("Skjermet $it") }
            val pdl = pdlTjeneste.person(brukerId).also { log.info("Person $it") }
            val gt = pdlTjeneste.gt(brukerId).also { log.info("GT $it") }
            PdlTilBrukerMapper.tilBruker(pdl, gt, skjermet)
        }

    fun brukerBulk(brukerIds: List<BrukerId>) = pdlTjeneste.personPipBulk(brukerIds)

    fun bruker(brukerId: BrukerId)  =
        run {
            val skjermet = egenAnsatt.erSkjermet(brukerId)
            PdlPipTilBrukerMapper.tilBruker(brukerId, pdlTjeneste.personPip(brukerId), skjermet)
        }
}
