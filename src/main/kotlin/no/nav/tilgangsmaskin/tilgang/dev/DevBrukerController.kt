package no.nav.tilgangsmaskin.tilgang.dev

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipClient
import no.nav.tilgangsmaskin.bruker.pdl.PdlTjeneste
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

private const val DEV_BRUKER_CONTROLLER_TAG_DESCRIPTION = "msg:openapi.dev.bruker.tag.description"

@UnprotectedRestController(value = ["/${ClusterConstants.DEV}/bruker/"])
@ConditionalOnNotProd
@Tag(name = "DevBrukerController", description = DEV_BRUKER_CONTROLLER_TAG_DESCRIPTION)
class DevBrukerController(private val brukere: BrukerTjeneste,
                          private val pdl: PdlTjeneste,
                          private val pip: PdlPipClient) {

    @GetMapping("person/pip/{id}")
    @Operation(summary = SUMMARY_PERSON_PIP, description = DESCRIPTION_PERSON_PIP)
    fun pip(@PathVariable id: String) = pip.person(id, id)


    @GetMapping("person/{id}")
    @Operation(summary = SUMMARY_PERSON, description = DESCRIPTION_PERSON)
    fun person(@PathVariable id: String) = pdl.medUtvidetFamilie(id)

    @PostMapping("brukere")
    @Operation(summary = SUMMARY_BRUKERE, description = DESCRIPTION_BRUKERE)
    fun brukere(@RequestBody ids: Set<String>) = brukere.brukere(ids)

    @PostMapping("brukeridentifikator")
    @Operation(summary = SUMMARY_BRUKERIDENTIFIKATOR, description = DESCRIPTION_BRUKERIDENTIFIKATOR)
    fun brukerIdentifikator(@RequestBody id: Identifikator) = brukere.brukerMedUtvidetFamilie(id.verdi)

    @GetMapping("bruker/{id}")
    @Operation(summary = SUMMARY_BRUKER, description = DESCRIPTION_BRUKER)
    fun bruker(@PathVariable id: String) = brukere.brukerMedUtvidetFamilie(id)

    companion object {
        private const val SUMMARY_PERSON_PIP = "msg:openapi.dev.bruker.person.pip.summary"
        private const val DESCRIPTION_PERSON_PIP = "msg:openapi.dev.bruker.person.pip.description"
        private const val SUMMARY_PERSON = "msg:openapi.dev.bruker.person.summary"
        private const val DESCRIPTION_PERSON = "msg:openapi.dev.bruker.person.description"
        private const val SUMMARY_BRUKERE = "msg:openapi.dev.bruker.brukere.summary"
        private const val DESCRIPTION_BRUKERE = "msg:openapi.dev.bruker.brukere.description"
        private const val SUMMARY_BRUKERIDENTIFIKATOR = "msg:openapi.dev.bruker.brukeridentifikator.summary"
        private const val DESCRIPTION_BRUKERIDENTIFIKATOR = "msg:openapi.dev.bruker.brukeridentifikator.description"
        private const val SUMMARY_BRUKER = "msg:openapi.dev.bruker.bruker.summary"
        private const val DESCRIPTION_BRUKER = "msg:openapi.dev.bruker.bruker.description"
    }


}