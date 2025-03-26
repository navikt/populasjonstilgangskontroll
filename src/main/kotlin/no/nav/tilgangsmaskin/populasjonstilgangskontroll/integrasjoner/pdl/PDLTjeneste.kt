package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.CacheableRetryingOnRecoverableService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPersonMapper.tilPerson

@CacheableRetryingOnRecoverableService(cacheNames = [PDL])
@Timed
class PDLTjeneste(private val adapter: PdlRestClientAdapter) {

    fun person(brukerId: BrukerId) = tilPerson(brukerId, adapter.person(brukerId.verdi)).let {
        it.copy(familie = Familie(it.foreldre, it.barn, søsken(it)))
    }

     fun personer (brukerIds: List<BrukerId>) =
         adapter.personer(brukerIds.map { it.verdi })
             .map { (brukerId, data) -> tilPerson(brukerId, data)
         }

    private fun søsken(person: Person) =
        personer(person.foreldre
            .map { it.brukerId })
            .flatMap { it.barn }
            .map { it.brukerId }
            .filterNot { it == person.brukerId }
            .map { FamilieMedlem(it,SØSKEN) }
}