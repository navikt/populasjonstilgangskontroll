package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.AktørId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.CacheableRetryingOnRecoverableService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPersonMapper.tilPerson

@CacheableRetryingOnRecoverableService(cacheNames = [PDL])
@Timed
class PDLTjeneste(private val adapter: PdlRestClientAdapter) {

    fun person(id: String) = tilPerson(adapter.person(id)).let {
        it.copy(familie = Familie(it.foreldre, it.barn, søsken(it)))
    }

    fun personer(brukerIds: List<BrukerId>) =
        adapter.personer(brukerIds.map(BrukerId::verdi))
            .map { respons ->
                tilPerson(respons.value).let {
                    it.copy(familie = Familie(it.foreldre, it.barn, søsken(it)))
                }
            }

    private fun søsken(person: Person) =
        adapter.personer(person.foreldre.map { it.brukerId.verdi })
            .flatMap { tilPerson(it.value).barn }
            .map { it.brukerId }
            .filterNot { it == person.brukerId }
            .map { FamilieMedlem(it, SØSKEN) }
}
