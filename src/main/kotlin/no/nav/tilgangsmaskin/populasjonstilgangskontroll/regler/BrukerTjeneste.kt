package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipTilBrukerMapper
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlTilBrukerMapper
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.springframework.stereotype.Service

@Service
class BrukerTjeneste(private val pdlTjeneste: PDLTjeneste,val egenAnsatt: SkjermingTjeneste) {

    fun bruker(brukerId: BrukerId) =
        runBlocking {
            val skjermet = egenAnsatt.erSkjermet(brukerId)
            val pdl = async { pdlTjeneste.person(brukerId) }
            val gt = async { pdlTjeneste.gt(brukerId) }
            PdlTilBrukerMapper.tilBruker(pdl.await(), gt.await(), skjermet)
        }

    fun brukerPip(brukerId: BrukerId)  =
        runBlocking {
            val skjermet = egenAnsatt.erSkjermet(brukerId)
           // PdlPipTilBrukerMapper.tilBruker(
                pdlTjeneste.personPip(brukerId)
           //     , skjermet)
        }

    fun bolk(brukerIds: List<BrukerId>) = pdlTjeneste.bolk(brukerIds)
}
