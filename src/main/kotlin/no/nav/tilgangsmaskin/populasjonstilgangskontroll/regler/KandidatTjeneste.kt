package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLSyncGraphQLClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingRestClientAdapter
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
class KandidatTjeneste(private val pdl: PDLSyncGraphQLClientAdapter, val egenAnsatt: SkjermingRestClientAdapter) {

     fun kandidat(fnr: Fødselsnummer) : Kandidat {
         return runBlocking {
             val pdlDeferred = async { pdl.person(fnr.verdi) }
             val gtDeferred = async { pdl.gt(fnr.verdi) }
             val skjermingDeferred = async { egenAnsatt.skjermetPerson(fnr.verdi) }
             KandidatMapper.mapToKandidat(fnr,pdlDeferred.await(), gtDeferred.await(), skjermingDeferred.await())
         }
    } // kan slå opp mer her senere
}
@Service
class SaksbehandlerTjeneste(private val entra: EntraTjeneste) {  // kan slå opp mer her senere

    private val log = getLogger(SaksbehandlerTjeneste::class.java)

    fun saksbehandler(navId: NavId) = entra.saksbehandler(navId).also {
        log.info(CONFIDENTIAL,"Saksbehandler: $it")
    }
}