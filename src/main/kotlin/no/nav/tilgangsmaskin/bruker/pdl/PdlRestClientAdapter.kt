package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.cache.ValKeyCacheAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
@Timed( value = "pdl_tjeneste", histogram = true, extraTags = ["backend", "pip"] )
class PdlRestClientAdapter(
    @Qualifier(PDL) restClient: RestClient,
    private val cf: PdlConfig,
    private val cache: ValKeyCacheAdapter,
    private val mapper: ObjectMapper) : AbstractRestClientAdapter(restClient, cf) {


    fun medUtvidetFamile(id: String, partnere: Set<FamilieMedlem>) =
        with(person(id)) {
            copy(familie = familie.copy(søsken = søsken(foreldre, brukerId.verdi), partnere = partnere))
        }

    fun person(brukerId: String) = tilPerson(get<PdlRespons>(cf.personURI, mapOf("ident" to brukerId)))

    fun personer(brukerIds: Set<String>) : List<Person> {
        cache.personer()

        return post<String>(cf.personerURI, brukerIds).let { res ->
            mapper.readValue<Map<String, PdlRespons?>>(res)
                .mapNotNull { it.value?.let { res -> it.key to res } }
                .toMap().map { tilPerson(it.value) }
        }
    }

    private fun søsken(foreldre: Set<FamilieMedlem>, ansattBrukerId: String): Set<FamilieMedlem> =
        personer(foreldre.map { it.brukerId.verdi }.toSet())
            .asSequence()
            .flatMap { it.barn }
            .filterNot { it.brukerId.verdi == ansattBrukerId }
            .map { FamilieMedlem(it.brukerId, SØSKEN) }
            .toSet()
}


