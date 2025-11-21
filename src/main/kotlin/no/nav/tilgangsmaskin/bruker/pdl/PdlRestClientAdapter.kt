package no.nav.tilgangsmaskin.bruker.pdl
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue

@Component
class PdlRestClientAdapter(
    @Qualifier(PDL) restClient: RestClient,
    private val cf: PdlConfig,
    private val cache: CacheClient,
    private val mapper: JsonMapper) : AbstractRestClientAdapter(restClient, cf) {

    @WithSpan
    fun medUtvidetFamile(id: String, partnere: Set<FamilieMedlem>) =
        with(person(id)) {
            copy(familie = familie.copy(søsken = søsken(foreldre, brukerId.verdi), partnere = partnere))
        }

    @WithSpan
    fun person(oppslagId: String) = tilPerson(oppslagId,get<PdlRespons>(cf.personURI, mapOf("ident" to oppslagId)))

    @WithSpan
    fun personer(identer: Set<String>) : Set<Person> {

        val fraCache = fraCache(identer)
        if (fraCache.size == identer.size) {
            return fraCache.values.toSet()
        }

        val fraRest = fraRest(identer  - fraCache.keys)


        cache.putMany(PDL_CACHE, fraRest,cf.varighet)
        return (fraRest.values + fraCache.values).toSet()

    }


    @WithSpan
    private fun fraCache(identer: Set<String>) : Map<String,Person>{
        if (identer.isEmpty()) {
            return emptyMap()
        }
        val innslag = cache.getMany<Person>(PDL_CACHE, identer)
        log.trace("Hentet ${innslag.size} person(er) fra cache for ${identer.size} ident(er)")
        return innslag
    }

    @WithSpan
    private fun fraRest(identer: Set<String>) : Map<String,Person> {
        if (identer.isEmpty()) {
            return emptyMap()
        }

        return  mapper.readValue<Map<String, PdlRespons?>>(post<String>(cf.personerURI, identer))
            .mapValues {
                    (oppslagId, pdlRespons) -> pdlRespons?.let{ tilPerson(oppslagId, it) }
            }
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


    companion object {
        private const val EXTRA = "medFamilie"
        private val PDL_CACHE = CachableConfig(PDL, EXTRA)
    }
}
