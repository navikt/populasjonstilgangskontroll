package no.nav.tilgangsmaskin.bruker.pdl

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPersoner
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverableService
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.Cacheable

@RetryingWhenRecoverableService
class PdlTjeneste(
    private val pip: PdlPipClient,
    private val graphQL: PdlSyncGraphQLClientAdapter,
    private val cache: CacheOperations,
    private val cf: PdlConfig,
) {

    private val log = getLogger(PdlTjeneste::class.java)

    @WithSpan
    @Cacheable(cacheNames = [PDL], key = "#root.methodName + ':' + #id")
    fun medUtvidetFamilie(id: String): Person {
        val person = person(id)
        val søsken = søsken(person)
        val partnere = graphQL.partnere(id)
        return person.copy(familie = person.familie.copy(søsken = søsken, partnere = partnere))
    }

    @WithSpan
    @Cacheable(cacheNames = [PDL], key = "#root.methodName + ':' + #id")
    fun medFamilie(id: String) = person(id)

    @WithSpan
    fun personer(identer: Set<String>): Set<Person> {
        if (identer.isEmpty()) {
            log.info("Bulk ingen personer å slå opp")
            return emptySet()
        }
        val fraCache = fraCache(identer)
        if (fraCache.size == identer.size) {
            return fraCache.values.toSet()
        }

        val fraRest = hentPersoner(identer - fraCache.keys).also {
            log.info("Hentet ${it.size} person(er) av ${identer.size - fraCache.size} mulige fra REST")
        }

        cache.putMany(PDL_MED_FAMILIE_CACHE, fraRest, cf.varighet)
        return (fraCache.values + fraRest.values).toSet()
    }

    private fun person(id: String) = tilPerson(id, pip.person(id, id))

    private fun hentPersoner(identer: Set<String>) =
        tilPersoner(pip.personer(identer))

    private fun søsken(person: Person): Set<FamilieMedlem> {
        if (person.foreldre.isEmpty()) return emptySet()
        return buildSet {
            hentPersoner(person.foreldre.map { it.brukerId.verdi }.toSet())
                .flatMap { it.value.barn }
                .filterNot { it.brukerId.verdi == person.brukerId.verdi }
                .mapTo(this) { FamilieMedlem(it.brukerId, SØSKEN) }
        }
    }

    private fun fraCache(identer: Set<String>) =
        cache.getMany(PDL_MED_FAMILIE_CACHE, identer, Person::class)
            .filterValues { it != null }
            .mapValues { it.value!! }.also {
                log.info("Hentet ${it.size} person(er) av ${identer.size} mulige fra CACHE")
            }
}
