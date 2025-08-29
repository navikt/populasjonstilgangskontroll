package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Tags
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.cache.CacheName
import no.nav.tilgangsmaskin.felles.rest.cache.ValkeyCacheClient
import no.nav.tilgangsmaskin.regler.motor.BulkCacheSuksessTeller
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class PdlRestClientAdapter(
    @Qualifier(PDL) restClient: RestClient,
    private val cf: PdlConfig,
    private val cache: ValkeyCacheClient,
    private val teller: BulkCacheSuksessTeller,
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
        val fraCache = cache.mget<Person>(PDL_CACHE, ids, EXTRA)
        if (fraCache.size == ids.size) {
            tell(true)
            return fraCache.values.toSet()
        }
        val fraRest = fraRest(ids  - fraCache.keys)
        cache.put(PDL_CACHE, fraRest, EXTRA)
        tell(false)
        return (fraRest.values + fraCache.values).toSet()
    }

    fun fraRest(ids: Set<String>) =
        if (ids.isEmpty()) {
            emptyMap()
        }
        else {
            mapper.readValue<Map<String, PdlRespons?>>(post<String>(cf.personerURI, ids))
                .mapValues {
                    (_, pdlRespons) -> pdlRespons?.let(::tilPerson)
                }
                .filterValues {
                    it != null
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

    private fun tell(status: Boolean) =
        teller.tell(Tags.of("name", PDL_CACHE.name,"suksess",status.toString()))

    companion object {
        const val EXTRA = "medNærmesteFamilie"
        private val PDL_CACHE = CacheName(PDL)
    }
}


