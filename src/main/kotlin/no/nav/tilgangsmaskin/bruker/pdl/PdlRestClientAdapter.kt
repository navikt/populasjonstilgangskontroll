package no.nav.tilgangsmaskin.bruker.pdl

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem
import no.nav.tilgangsmaskin.bruker.Familie.FamilieMedlem.FamilieRelasjon.SØSKEN
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPerson
import no.nav.tilgangsmaskin.bruker.pdl.PdlPersonMapper.tilPersoner
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
    private val mapper: JsonMapper,
) : AbstractRestClientAdapter(restClient, cf) {

    @WithSpan
    fun person(oppslagId: String) =
        tilPerson(oppslagId, get<PdlRespons>(cf.personURI, mapOf("ident" to oppslagId, IDENTIFIKATOR to oppslagId)))

    @WithSpan
    fun personer(identer: Set<String>) =
        tilPersoner(mapper.readValue<Map<String, PdlRespons?>>(post<String>(cf.personerURI, identer)))

    @WithSpan
    fun søsken(person: Person): Set<FamilieMedlem> {
        if (person.foreldre.isEmpty()) return emptySet()
        return buildSet {
            personer(person.foreldre.map { it.brukerId.verdi }.toSet())
                .flatMap { it.value.barn }
                .filterNot { it.brukerId.verdi == person.brukerId.verdi }
                .mapTo(this) { FamilieMedlem(it.brukerId, SØSKEN) }
        }
    }
}

