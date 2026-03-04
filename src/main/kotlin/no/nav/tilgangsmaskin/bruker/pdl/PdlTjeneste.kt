package no.nav.tilgangsmaskin.bruker.pdl

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverable
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@RetryingWhenRecoverable
@Service
class PdlTjeneste(
    private val adapter: PdlRestClientAdapter,
    private val graphQL: PdlSyncGraphQLClientAdapter,
    private val cache: CacheOperations,
    private val cf: PdlConfig) {

    private val log = getLogger(PdlTjeneste::class.java)

    @WithSpan
    @Cacheable(cacheNames = [PDL], key = "#root.methodName + ':' + #id")
    fun medUtvidetFamile(id: String): Person {
        val person = adapter.person(id)
        val søsken = adapter.søsken(person)
        val partnere = graphQL.partnere(id)
        return person.copy(familie = person.familie.copy(søsken = søsken, partnere = partnere))
    }

    @WithSpan
    @Cacheable(cacheNames = [PDL], key = "#root.methodName + ':' + #id")
    fun medFamilie(id: String) =
        adapter.person(id)

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

        val fraRest = adapter.personer(identer - fraCache.keys).also {
            log.info("Hentet ${it.size} person(er) av ${identer.size - fraCache.size} mulige fra REST")
        }

        cache.putMany(PDL_MED_FAMILIE_CACHE, fraRest, cf.varighet)
        return (fraCache.values + fraRest.values).toSet()
    }

    private fun fraCache(identer: Set<String>) =
        cache.getMany(PDL_MED_FAMILIE_CACHE, identer, Person::class)
            .filterValues { it != null }
            .mapValues { it.value!! }.also {
                log.info("Hentet ${it.size} person(er) av ${identer.size} mulige fra CACHE")
            }
}
