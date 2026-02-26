package no.nav.tilgangsmaskin.bruker.pdl
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_MED_FAMILIE_CACHE
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPersoner
import no.nav.tilgangsmaskin.felles.cache.CacheOperations
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.DefaultRestErrorHandler.Companion.IDENTIFIKATOR
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue

@Component
class PdlRestClientAdapter(
    @Qualifier(PDL) restClient: RestClient,
    private val cf: PdlConfig,
    private val cache: CacheOperations,
    private val mapper: JsonMapper) : AbstractRestClientAdapter(restClient, cf) {

    fun medUtvidetFamile(id: String, partnere: Set<FamilieMedlem>) =
        with(person(id)) {
            copy(familie = familie.copy(søsken = søsken(foreldre, brukerId.verdi), partnere = partnere))
        }

    fun person(oppslagId: String) = tilPerson(oppslagId,get<PdlRespons>(cf.personURI, mapOf("ident" to oppslagId,IDENTIFIKATOR to oppslagId)))

    fun personer(identer: Set<String>) : Set<Person> {

        val fraCache = fraCache(identer)
        if (!fraCache.isEmpty())  {
            log.trace("Hentet ${fraCache.size} person(er) av ${identer.size} mulige fra cache")
        }
        if (fraCache.size == identer.size) {
            return fraCache.values.toSet()
        }
        val fraRest = fraRest(identer  - fraCache.keys)
        if (!fraRest.isEmpty()) {
            log.trace("Hentet ${fraRest.size} person(er) av ${identer.size - fraCache.keys.size} mulige fra REST")
        }

        cache.putMany(fraRest, PDL_MED_FAMILIE_CACHE, cf.varighet)
        return (fraRest.values + fraCache.values).toSet()
    }


    private fun fraCache(identer: Set<String>) : Map<String,Person>{
        if (identer.isEmpty()) {
            return emptyMap()
        }
        val innslag = cache.getMany(identer, PDL_MED_FAMILIE_CACHE, Person::class)
        return innslag.filterValues { it != null }.mapValues { it.value!! }
    }

    private fun fraRest(identer: Set<String>) =
        if (identer.isEmpty()) {
            emptyMap()
        }
        else {
            tilPersoner(mapper.readValue<Map<String, PdlRespons?>>(post<String>(cf.personerURI, identer)))
        }

    @WithSpan
    private fun søsken(foreldre: Set<FamilieMedlem>, ansattBrukerId: String) =
        personer(foreldre.map { it.brukerId.verdi }.toSet())
            .flatMap { it.barn }
            .filterNot { it.brukerId.verdi == ansattBrukerId }
            .map { FamilieMedlem(it.brukerId, SØSKEN) }
            .toSet()
}
