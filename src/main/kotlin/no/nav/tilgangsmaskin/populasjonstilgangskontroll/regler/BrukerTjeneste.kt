package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLSyncGraphQLClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingRestClientAdapter
import org.springframework.stereotype.Service

@Service
class BrukerTjeneste(private val pdl: PDLSyncGraphQLClientAdapter, val egenAnsatt: SkjermingRestClientAdapter) {

     fun bruker(fnr: Fødselsnummer) : Bruker {
         return runBlocking {
             val pdlDeferred = async { pdl.person(fnr.verdi) }
             val gtDeferred = async { pdl.gt(fnr.verdi) }
             val skjermingDeferred = async { egenAnsatt.erSkjermet(fnr.verdi) }
             BrukerMapper.mapToBruker(fnr,pdlDeferred.await(), gtDeferred.await(), skjermingDeferred.await())
         }
    } // kan slå opp mer her senere
}
