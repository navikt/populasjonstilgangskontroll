package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlSyncGraphQLClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlTilBrukerMapper
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingRestClientAdapter
import org.springframework.stereotype.Service

@Service
class BrukerTjeneste(private val pdlAdapter: PdlSyncGraphQLClientAdapter, val egenAnsatt: SkjermingRestClientAdapter) {

     fun bruker(fnr: Fødselsnummer) : Bruker {
         return runBlocking {
             val pdl = async { pdlAdapter.person(fnr.verdi) }
             val gt = async { pdlAdapter.gt(fnr.verdi) }
             val skjerming = async { egenAnsatt.erSkjermet(fnr.verdi) }
             PdlTilBrukerMapper.tilBruker(pdl.await(), gt.await(), skjerming.await())
         }
    } // kan slå opp mer her senere
}
