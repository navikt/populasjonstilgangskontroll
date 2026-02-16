package no.nav.tilgangsmaskin.bruker.pdl
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.DefaultRestErrorHandler.Companion.IDENTIFIKATOR
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.json.JsonMapper

@Component
class PdlRestClientAdapter(
    @Qualifier(PDL) restClient: RestClient,
    private val cf: PdlConfig,
    private val cache: CacheOperations,
    private val mapper: JsonMapper) : AbstractRestClientAdapter(restClient, cf) {

    @WithSpan
    fun medUtvidetFamile(id: String, partnere: Set<FamilieMedlem>) =
        with(person(id)) {
            copy(familie = familie.copy(søsken = søsken(foreldre, brukerId.verdi), partnere = partnere))
        }

    @WithSpan
    fun person(oppslagId: String) = tilPerson(oppslagId,get<PdlRespons>(cf.personURI, mapOf("ident" to oppslagId,IDENTIFIKATOR to oppslagId)))

    @WithSpan
    fun personer(identer: Set<String>) : Set<Person> {

        val fraCache = fraCache(identer)
        if (fraCache.size == identer.size) {
            return fraCache.values.toSet()
        }

        val fraRest = fraRest(identer  - fraCache.keys)

        cache.putMany(fraRest, PDL_MED_FAMILIE_CACHE, cf.varighet)
        return (fraRest.values + fraCache.values).toSet()
    }


    @WithSpan
    private fun fraCache(identer: Set<String>) : Map<String,Person>{
        if (identer.isEmpty()) {
            return emptyMap()
        }
        val innslag = cache.getMany(identer, PDL_MED_FAMILIE_CACHE, Person::class)
        log.trace("Hentet ${innslag.size} person(er) fra cache for ${identer.size} ident(er)")
        return innslag.filterValues { it != null }.mapValues { it.value!! }
    }

    @WithSpan
    private fun fraRest(identer: Set<String>) : Map<String,Person> {
        if (identer.isEmpty()) {
            return emptyMap()
        }

        return  mapper.readValue(post<String>(cf.personerURI, identer), object : TypeReference<Map<String, PdlRespons?>>() {})
            .mapValues { (oppslagId, pdlRespons) -> pdlRespons?.let{ tilPerson(oppslagId, it) } }
            .filterValues {
                it != null
            }
            .mapValues {
                it.value!!
            }
            .also {
                log.trace("Hentet ${it.size} person(er) fra REST for ${identer.size} ident(er)")
            }
    }


    @WithSpan
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
}
