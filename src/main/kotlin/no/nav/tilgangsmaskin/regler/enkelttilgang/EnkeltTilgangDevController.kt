package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.swagger.v3.oas.annotations.Operation
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangDevController.Companion.DEV_ENKELT_CONTROLLER_TAG_DESCRIPTION
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping


@DevController(
    value = ["/${DEV}/enkelt/"],
    name = "DevEnkelttilgangController",
    description = DEV_ENKELT_CONTROLLER_TAG_DESCRIPTION
)
class EnkeltTilgangDevController(private val enkelt: EnkeltTilgangTjeneste,
                                 private val adapter: EnkeltTilgangJPAAdapter) {

    @PostMapping("{ansattId}/{brukerId}")
    @Operation(summary = SUMMARY_ENKELT, description = DESCRIPTION_ENKELT)
    fun enkelt(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) =
        enkelt.registrerTilgang(ansattId, EnkeltTilgangData(brukerId, "test"))

    @GetMapping("sjekk/{ansattId}/{brukerId}")
    @Operation(summary = SUMMARY_HAR, description = DESCRIPTION_HAR)
    fun harTilgang(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) =
        enkelt.harTilgang(ansattId, brukerId)

    @GetMapping("gjeldende/{ansattId}/{brukerId}")
    @Operation(summary = SUMMARY_GJELDENDE, description = DESCRIPTION_GJELDENDE)
    fun gjeldendeTilgang(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) =
        adapter.gjeldendeTilgang(ansattId.verdi, brukerId.verdi, emptyList())

    companion object {
        private const val DEV_ENKELT_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.dev.enkelt.tag.description"
        private const val DESCRIPTION_ENKELT = "${MSG}openapi.dev.enkelt.description"
        private const val SUMMARY_ENKELT = "${MSG}openapi.dev.enkelt.summary"
        private const val SUMMARY_HAR = "${MSG}openapi.dev.enkelt.har"
        private const val DESCRIPTION_HAR = "${MSG}openapi.dev.enkelt.har.description"
        private const val SUMMARY_GJELDENDE = "${MSG}openapi.dev.enkelt.gjeldende.summary"
        private const val DESCRIPTION_GJELDENDE = "${MSG}openapi.dev.enkelt.gjeldende.description"

    }
}
