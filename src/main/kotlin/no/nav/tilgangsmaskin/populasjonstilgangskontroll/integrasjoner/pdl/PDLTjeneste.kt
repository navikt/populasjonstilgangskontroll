package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.CacheableRetryingOnRecoverableService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlConfig.Companion.PDL

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.IrrecoverableRestException

@CacheableRetryingOnRecoverableService(cacheNames = [PDL])
@Timed
class PDLTjeneste(private val pdlAdapter: PdlPipRestClientAdapter) {

    fun person(brukerId: BrukerId) = pdlAdapter.person(brukerId.verdi)

    fun personer(brukerIds: List<BrukerId>) =
        runBlocking {
            brukerIds.map {
                async {
                    try {
                    person(it)
                    } catch (e: IrrecoverableRestException) {
                       null
                    }
                }
            }.awaitAll().filterNotNull()
        }
}