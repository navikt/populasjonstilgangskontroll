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
class PDLTjeneste(private val adapter: PdlPipRestClientAdapter) {

    fun person(brukerId: BrukerId) = adapter.person(brukerId.verdi)

     fun personer1 (brukerIds: List<BrukerId>) = adapter.personer(brukerIds.map { it.verdi })

    fun personer(brukerIds: List<BrukerId>) =
        runBlocking {
            brukerIds.map {
                async {
                    runCatching { person(it) }
                        .getOrNull()
                }
            }.awaitAll().filterNotNull()
        }
}