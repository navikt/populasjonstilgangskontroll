package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.cache.ValkeyCacheClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
@Timed( value = "pdl_tjeneste", histogram = true, extraTags = ["backend", "pip"] )
class PdlRestClientAdapter(
    @Qualifier(PDL) restClient: RestClient,
    private val cf: PdlConfig,
    private val cache: ValkeyCacheClient,
    private val mapper: ObjectMapper) : AbstractRestClientAdapter(restClient, cf) {


    fun medUtvidetFamile(id: String, partnere: Set<FamilieMedlem>) =
        with(person(id)) {
            copy(familie = familie.copy(søsken = søsken(foreldre, brukerId.verdi), partnere = partnere))
        }

    fun person(id: String) = tilPerson(get<PdlRespons>(cf.personURI, mapOf("ident" to id)))

    fun personer(ids: Set<String>) : List<Person> {
        //. Slå opp i cache
        // Slå opp fra tjenesten
        // oppdater cache
        // slå sammen resultater

        val personerFraCache = fraCache(ids)

        val personerFraRest =  mapper.readValue<Map<String, PdlRespons?>>(post<String>(cf.personerURI, ids))
            .mapNotNull { (_, res) -> res?.let { tilPerson(it) } }
        return personerFraRest // TODO
    }

    private fun fraCache(ids: Set<String>) : Set<Person> {
        val personerFraCache = cache.mget<Person>(PDL, ids, "medNærmesteFamilie").map { it.second
        }.toSet()

        log.info("Fant ${personerFraCache.size} personer fra PDL cache for ${ids.size} ident(er)")
        if (ids.size == personerFraCache.size) {
            log.info("Alle ${ids.size} personer er i PDL cache, returnerer ${personerFraCache.size} personer")
        }
        else {
            log.info("Ikke alle ${ids.size} personer er i PDL cache, slår opp resterende  ${ids.size - personerFraCache.size} ident(er) i PDL")
        }
        return personerFraCache
    }

    private fun søsken(foreldre: Set<FamilieMedlem>, ansattBrukerId: String): Set<FamilieMedlem> =
        personer(foreldre.map { it.brukerId.verdi }.toSet())
            .asSequence()
            .flatMap { it.barn }
            .filterNot { it.brukerId.verdi == ansattBrukerId }
            .map { FamilieMedlem(it.brukerId, SØSKEN) }
            .toSet()
}


