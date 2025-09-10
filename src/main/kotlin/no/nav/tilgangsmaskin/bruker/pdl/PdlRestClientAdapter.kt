package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.cache.CacheConfig
import no.nav.tilgangsmaskin.felles.rest.cache.ValkeyCacheClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class PdlRestClientAdapter(
    @Qualifier(PDL) restClient: RestClient,
    private val cf: PdlConfig,
    private val cache: ValkeyCacheClient,
    private val mapper: ObjectMapper) : AbstractRestClientAdapter(restClient, cf) {

    @WithSpan
    fun medUtvidetFamile(id: String, partnere: Set<FamilieMedlem>) =
        with(person(id)) {
            copy(familie = familie.copy(søsken = søsken(foreldre, brukerId.verdi), partnere = partnere))
        }

    @WithSpan
    fun person(id: String) = tilPerson(get<PdlRespons>(cf.personURI, mapOf("ident" to id)))

    @WithSpan
    fun personer(ids: Set<String>) : Set<Person> {
        val fraCache = fraCache(ids)
        if (fraCache.size == ids.size) {
            return fraCache.values.toSet()
        }
        val fraRest = fraRest(ids  - fraCache.keys)
        log.trace("Bulk hentet ${fraRest.size} person(er) fra rest for ${ids.size} ident(er)")
        cache.putMany(PDL_CACHE, fraRest, cf.varighet)
        return (fraRest.values + fraCache.values).toSet()
    }


    private fun fraCache(ids: Set<String>) : Map<String,Person>{
        if (ids.isEmpty()) {
            return emptyMap()
        }
        val innslag = cache.getMany<Person>(PDL_CACHE, ids)
        log.trace("Bulk hentet ${innslag.size} person(er) fra cache for ${ids.size} ident(er)")
        return innslag
    }

    private fun fraRest(ids: Set<String>) : Map<String,Person> {
        if (ids.isEmpty()) {
            return emptyMap()
        }

        return  mapper.readValue<Map<String, PdlRespons?>>(post<String>(cf.personerURI, ids))
            .filterValues {
                it != null
            }
            .mapValues {
                    (_, pdlRespons) -> pdlRespons?.let(::tilPerson)
            }
            .mapValues {
                it.value!!
            }
            .also {
                log.trace("Hentet ${it.size} person(er) fra REST for ${ids.size} ident(er)")
            }
    }


    private fun søsken(foreldre: Set<FamilieMedlem>, ansattBrukerId: String): Set<FamilieMedlem> =
        personer(foreldre.map { it.brukerId.verdi }.toSet())
            .asSequence()
            .flatMap {
                it.barn
            }
            .filterNot {
                it.brukerId.verdi == ansattBrukerId
            }
            .map {
                FamilieMedlem(it.brukerId, SØSKEN)
            }
            .toSet()


    companion object {
        private const val EXTRA = "medNærmesteFamilie"
        private val PDL_CACHE = CacheConfig(PDL, EXTRA)
    }
}




