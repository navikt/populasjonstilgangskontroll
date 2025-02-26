package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRestClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipTilBrukerMapper
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlSyncGraphQLClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlTilBrukerMapper
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.springframework.stereotype.Service

@Service
class BrukerTjeneste(private val pdlAdapter: PdlSyncGraphQLClientAdapter, val pipRestClientAdapter: PdlPipRestClientAdapter,val egenAnsatt: SkjermingTjeneste) {

    /* TODO Her må vi etterhvert slå opp skjerming først, og så hente bruker og eventuelle releasjoner om skjerming er satt  etterpå */
    fun brukerOld(brukerId: BrukerId) =
        runBlocking {
            val pdl = async { pdlAdapter.person(brukerId.verdi) }
            val gt = async { pdlAdapter.gt(brukerId.verdi) }
            val skjerming = async { egenAnsatt.erSkjermet(brukerId) }
            PdlTilBrukerMapper.tilBruker(pdl.await(), gt.await(), skjerming.await())
        }

    fun bruker(brukerId: BrukerId)  =
        runBlocking {
            val pdl = async { pipRestClientAdapter.person(brukerId.verdi)}
            val skjerming = async { egenAnsatt.erSkjermet(brukerId)
            }
            PdlPipTilBrukerMapper.tilBruker(pdl.await(), skjerming.await())
    }
    fun bolk(brukerIds: List<BrukerId>) = pipRestClientAdapter.bolk(brukerIds.map { it.verdi })
}
