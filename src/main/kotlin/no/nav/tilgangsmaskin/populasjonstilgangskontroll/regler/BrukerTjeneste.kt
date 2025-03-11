package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipTilBrukerMapper
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BrukerTjeneste(private val pdlTjeneste: PDLTjeneste,val egenAnsatt: SkjermingTjeneste) {

    private val log = LoggerFactory.getLogger(BrukerTjeneste::class.java)

    fun brukerBulk(brukerIds: List<BrukerId>) = pdlTjeneste.personPipBulk(brukerIds)

    fun bruker(brukerId: BrukerId)  =
        run {
            val skjermet = egenAnsatt.erSkjermet(brukerId)
            val pip = pdlTjeneste.person(brukerId)
            tilBruker(brukerId, pip, skjermet)
        }
}
