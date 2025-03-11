package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.RetryingOnRecoverableCacheableService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlConfig.Companion.PDL

@RetryingOnRecoverableCacheableService(cacheNames = [PDL])
class PDLTjeneste(private val pipAdapter: PdlPipRestClientAdapter) {

    fun person(brukerId: BrukerId) = pipAdapter.person(brukerId.verdi)

    fun personPipBulk(brukerIds: List<BrukerId>) = pipAdapter.personBulk(brukerIds.map { it.verdi })

}