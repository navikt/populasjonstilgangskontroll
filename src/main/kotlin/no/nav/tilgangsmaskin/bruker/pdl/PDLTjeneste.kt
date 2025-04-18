package no.nav.tilgangsmaskin.bruker.pdl

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPartner
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.felles.CacheableRetryingOnRecoverableService

@CacheableRetryingOnRecoverableService(cacheNames = [PDL])
@Timed
class PDLTjeneste(private val adapter: PdlRestClientAdapter, private val graphQL: PdlSyncGraphQLClientAdapter) {

    fun utvidetFamile(id: String) = tilPerson(adapter.person(id)).let {
        it.copy(familie = Familie(it.foreldre, it.barn, søsken(it), partnere(id)))
    }

    fun nærmesteFamilie(id: String) = tilPerson(adapter.person(id))

    fun personer(brukerIds: Set<String>) =
        adapter.personer(brukerIds)
            .map { respons ->
                tilPerson(respons.value).let {
                    it.copy(familie = Familie(it.foreldre, it.barn))
                }
            }

    private fun partnere(id: String) =
        graphQL.sivilstand(id).sivilstand.mapNotNull {
            it.relatertVedSivilstand?.let { brukerId ->
                FamilieMedlem(BrukerId(brukerId), tilPartner(it.type))
            }
        }.toSet()

    private fun søsken(person: Person) =
        adapter.personer(person.foreldre.map { it.brukerId.verdi }.toSet())
            .asSequence()
            .flatMap { tilPerson(it.value).barn }
            .map { it.brukerId }
            .filterNot { it == person.brukerId }
            .map { FamilieMedlem(it, SØSKEN) }.toSet()
}
