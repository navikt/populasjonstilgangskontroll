package no.nav.tilgangsmaskin.bruker.pdl

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPartner
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverableService
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.Cacheable

@RetryingOnRecoverableService
@Cacheable(cacheNames = [PDL])
class PDLTjeneste(private val adapter: PdlRestClientAdapter, private val graphQL: PdlSyncGraphQLClientAdapter) {

    private val log = getLogger(javaClass)

    fun medNærmesteFamilie(id: String) = tilPerson(adapter.person(id))

    fun personer(brukerIds: Set<String>) : List<Person> {
        log.debug("Bulk henter personer for  ${brukerIds.map { it.maskFnr() }.joinToString { "," }}")
        return adapter.personer(brukerIds).map { tilPerson(it.value) }
    }

    fun partnere(id: String) =
        graphQL.sivilstand(id).sivilstand
            .mapNotNull {
                it.relatertVedSivilstand?.let { brukerId ->
                    FamilieMedlem(BrukerId(brukerId), tilPartner(it.type))
                }
            }.toSet()

    fun søsken(foreldre: Set<FamilieMedlem>, ansatt: BrukerId) =
        adapter.personer(foreldre.map { it.brukerId.verdi }.toSet())
            .asSequence()
            .flatMap { tilPerson(it.value).barn }
            .filter { it.brukerId != ansatt }
            .map { FamilieMedlem(it.brukerId, SØSKEN) }
            .toSet()

}
