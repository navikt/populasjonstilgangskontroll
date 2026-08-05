package no.nav.tilgangsmaskin.bruker

import io.swagger.v3.oas.annotations.Operation
import no.nav.tilgangsmaskin.bruker.BrukerController.Companion.DEV_BRUKER_CONTROLLER_TAG_DESCRIPTION
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipClient
import no.nav.tilgangsmaskin.bruker.pdl.PdlTjeneste
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody


@DevController(
    value = ["/${DEV}/bruker/"],
    name = "BrukerController",
    description = DEV_BRUKER_CONTROLLER_TAG_DESCRIPTION
)
class BrukerController(private val bruker: BrukerTjeneste, private val pdl: PdlTjeneste, private val pip: PdlPipClient) {

    @GetMapping("person/pip/{id}")
    @Operation(summary = SUMMARY_PERSON_PIP, description = DESCRIPTION_PERSON_PIP)
    fun pip(@PathVariable id: String) =
        pip.person(id, id)

    @GetMapping("person/{id}")
    @Operation(summary = SUMMARY_PERSON, description = DESCRIPTION_PERSON)
    fun person(@PathVariable id: String) =
        pdl.medUtvidetFamilie(id)

    @PostMapping("brukere")
    @Operation(summary = SUMMARY_BRUKERE, description = DESCRIPTION_BRUKERE)
    fun brukere(@RequestBody ids: Set<String>) =
        bruker.brukere(ids)

    @PostMapping("brukeridentifikator")
    @Operation(summary = SUMMARY_BRUKERIDENTIFIKATOR, description = DESCRIPTION_BRUKERIDENTIFIKATOR)
    fun brukerIdentifikator(@RequestBody id: Identifikator) =
        bruker.medUtvidetFamilie(id.verdi)

    @GetMapping("bruker/{id}")
    @Operation(summary = SUMMARY_BRUKER, description = DESCRIPTION_BRUKER)
    fun bruker(@PathVariable id: String) =
        bruker.medUtvidetFamilie(id)

    companion object {
        private const val DEV_BRUKER_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.dev.bruker.tag.description"
        private const val SUMMARY_PERSON_PIP = "${MSG}openapi.dev.bruker.person.pip.summary"
        private const val DESCRIPTION_PERSON_PIP = "${MSG}openapi.dev.bruker.person.pip.description"
        private const val SUMMARY_PERSON = "${MSG}openapi.dev.bruker.person.summary"
        private const val DESCRIPTION_PERSON = "${MSG}openapi.dev.bruker.person.description"
        private const val SUMMARY_BRUKERE = "${MSG}openapi.dev.bruker.brukere.summary"
        private const val DESCRIPTION_BRUKERE = "${MSG}openapi.dev.bruker.brukere.description"
        private const val SUMMARY_BRUKERIDENTIFIKATOR = "${MSG}openapi.dev.bruker.brukeridentifikator.summary"
        private const val DESCRIPTION_BRUKERIDENTIFIKATOR = "${MSG}openapi.dev.bruker.brukeridentifikator.description"
        private const val SUMMARY_BRUKER = "${MSG}openapi.dev.bruker.bruker.summary"
        private const val DESCRIPTION_BRUKER = "${MSG}openapi.dev.bruker.bruker.description"
    }


}