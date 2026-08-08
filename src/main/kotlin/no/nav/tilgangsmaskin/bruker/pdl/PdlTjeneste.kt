package no.nav.tilgangsmaskin.bruker.pdl

import io.micrometer.observation.annotation.Observed
import no.nav.tilgangsmaskin.bruker.Familie
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.*
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPersoner
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipConfig.Companion.PDLPIP
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.cache.getMany
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableRestService
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.service.registry.ImportHttpServices

@Observed
@RetryingWhenRecoverableRestService
@ImportHttpServices(types = [PdlPipClient::class], group = PDLPIP)
class PdlTjeneste(
    private val pip: PdlPipClient,
    private val graphQL: PdlSyncGraphQLClientAdapter,
    private val cache: CacheOperations
) {

    private val log = getLogger(PdlTjeneste::class.java)

    @Cacheable(cacheNames = [PDL], key = "#root.methodName + ':' + #id")
    fun medUtvidetFamilie(id: String): Person {
        val person = person(id)
        val søsken = søsken(person)
        val partnere = graphQL.partnere(id)
        return person.copy(familie = Familie(person.familie.medlemmer + søsken + partnere))
    }

    @Cacheable(cacheNames = [PDL], key = "#root.methodName + ':' + #id")
    fun medFamilie(id: String) = person(id)

    fun personer(identer: Set<String>): Set<Person> {
        if (identer.isEmpty()) {
            return emptySet<Person>().also {
                log.trace("Bulk ingen personer å slå opp")

            }
        }
        val fraCache = fraCache(identer)
        if (fraCache.size == identer.size) {
            return fraCache.values.toSet()
        }

        val fraRest = hentPersoner(identer - fraCache.keys).also {
            log.trace("Hentet ${it.size} person(er) av ${identer.size - fraCache.size} mulige fra REST")
        }

        cache.putMany(PDL_MED_FAMILIE_CACHE, fraRest)
        return (fraCache.values + fraRest.values).toSet()
    }

    private fun person(id: String) =
        tilPerson(id, pip.person(id, id))

    private fun hentPersoner(identer: Set<String>) =
        tilPersoner(pip.personer(identer))

    private fun søsken(person: Person) =
        person.foreldre
            .map { it.brukerId.verdi }
            .toSet()
            .takeIf { it.isNotEmpty() }
            ?.let(::hentPersoner)
            ?.values
            ?.flatMap { it.barn }
            ?.filterNot { it.brukerId == person.brukerId }
            ?.map { FamilieMedlem(it.brukerId, SØSKEN) }
            ?.toSet()
            ?: emptySet()

    private fun fraCache(identer: Set<String>) =
        cache.getMany<Person>(PDL_MED_FAMILIE_CACHE, identer)
            .filterValues { it != null }
            .mapValues { it.value!! }.also {
                log.trace("Hentet ${it.size} person(er) av ${identer.size} mulige fra CACHE")
            }
}
