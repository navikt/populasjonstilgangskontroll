package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.CacheableRetryingOnRecoverableService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPersonMapper.tilPerson

@CacheableRetryingOnRecoverableService(cacheNames = [PDL])
@Timed
class PDLTjeneste(private val adapter: PdlRestClientAdapter) {
it
    fun person(brukerId: BrukerId) = tilPerson(brukerId, adapter.person(brukerId.verdi))

    fun søsken(brukerId: BrukerId) =
        personer(person(brukerId).foreldre
            .map { it.brukerId })
            .flatMap { it.barn }
            .map { it.brukerId }
            .filterNot { it == brukerId }

     fun personer (brukerIds: List<BrukerId>) =
         adapter.personer(brukerIds.map { it.verdi })
             .map { (brukerId, data) -> tilPerson(brukerId, data)
         }
}