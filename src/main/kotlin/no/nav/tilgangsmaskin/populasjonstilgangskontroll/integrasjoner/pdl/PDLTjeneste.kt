package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.CacheableRetryingOnRecoverableService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlConfig.Companion.PDL

@CacheableRetryingOnRecoverableService(cacheNames = [PDL])
@Timed
class PDLTjeneste(private val pdlAdapter: PdlPipRestClientAdapter) {

    fun person(brukerId: BrukerId) = pdlAdapter.person(brukerId.verdi)

    fun personPipBulk(brukerIds: List<BrukerId>) = pdlAdapter.personBulk(brukerIds.map { it.verdi })

}