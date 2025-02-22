package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlSyncGraphQLClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlTilBrukerMapper
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingRestClientAdapter
import org.springframework.stereotype.Service

@Service
class BrukerTjeneste(private val pdlAdapter: PdlSyncGraphQLClientAdapter, val egenAnsatt: SkjermingRestClientAdapter) {

    fun bruker(brukerId: BrukerId) =
        runBlocking {
            val pdl = async { pdlAdapter.person(brukerId.verdi) }
            val gt = async { pdlAdapter.gt(brukerId.verdi) }
            val skjerming = async { egenAnsatt.erSkjermet(brukerId.verdi) }
            PdlTilBrukerMapper.tilBruker(pdl.await(), gt.await(), skjerming.await())
        }
}
