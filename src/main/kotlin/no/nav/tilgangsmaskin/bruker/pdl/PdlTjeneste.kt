package no.nav.tilgangsmaskin.bruker.pdl

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverable
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@RetryingWhenRecoverable
@Service
class PdlTjeneste(
    private val adapter: PdlRestClientAdapter,
    private val graphQL: PdlSyncGraphQLClientAdapter,
    private val cache: CacheOperations,
    private val cf: PdlConfig,
) {
    @Lazy
    @Autowired
    private lateinit var self: PdlTjeneste

    @WithSpan
    @Cacheable(cacheNames = [PDL], key = "#root.methodName + ':' + #id")
    fun medUtvidetFamile(id: String): Person {
        val person = adapter.person(id)
        val søsken = søsken(person.foreldre, person.brukerId.verdi)
        val partnere = graphQL.partnere(id)
        return person.copy(familie = person.familie.copy(søsken = søsken, partnere = partnere))
    }

    @WithSpan
    @Cacheable(cacheNames = [PDL], key = "#root.methodName + ':' + #id")
    fun medFamilie(id: String) = adapter.person(id)

    @WithSpan
    fun personer(identer: Set<String>): Set<Person> {
        val fraCache = fraCache(identer)
        if (fraCache.isNotEmpty()) {
            log.trace("Hentet ${fraCache.size} person(er) av ${identer.size} mulige fra CACHE")
        }
        if (fraCache.size == identer.size) return fraCache.values.toSet()

        val fraRest = adapter.personer(identer - fraCache.keys)
        if (fraRest.isNotEmpty()) {
            log.trace("Hentet ${fraRest.size} person(er) av ${identer.size - fraCache.size} mulige fra REST")
        }

        cache.putMany(PDL_MED_FAMILIE_CACHE, fraRest, cf.varighet)
        return (fraRest.values + fraCache.values).toSet()
    }

    private fun fraCache(identer: Set<String>): Map<String, Person> {
        if (identer.isEmpty()) return emptyMap()
        return cache.getMany(PDL_MED_FAMILIE_CACHE, identer, Person::class)
            .filterValues { it != null }
            .mapValues { it.value!! }
    }

    private fun søsken(foreldre: Set<FamilieMedlem>, ansattBrukerId: String) =
        self.personer(foreldre.map { it.brukerId.verdi }.toSet())
            .flatMap { it.barn }
            .filterNot { it.brukerId.verdi == ansattBrukerId }
            .map { FamilieMedlem(it.brukerId, SØSKEN) }
            .toSet()

    companion object {
        private val log = getLogger(PdlTjeneste::class.java)
    }
}


