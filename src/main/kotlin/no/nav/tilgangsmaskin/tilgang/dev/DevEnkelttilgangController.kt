package no.nav.tilgangsmaskin.tilgang.dev

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangData
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangJPAAdapter
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.tilgang.dev.DevEnkelttilgangController.Companion.DEV_ENKELT_CONTROLLER_TAG_DESCRIPTION
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping


@UnprotectedRestController(value = ["/${DEV}/enkelt/"])
@ConditionalOnNotProd
@Tag(name = "DevEnkelttilgangController", description = DEV_ENKELT_CONTROLLER_TAG_DESCRIPTION)
class DevEnkelttilgangController(private val enkelt: EnkeltTilgangTjeneste,
                                 private val adapter: EnkeltTilgangJPAAdapter) {

    @PostMapping("{ansattId}/{brukerId}")
    @Operation(summary = SUMMARY_ENKELT, description = DESCRIPTION_ENKELT)
    fun enkelt(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) =
        enkelt.registrerEnkeltTilgang(ansattId, EnkeltTilgangData(brukerId, "test"))

    @GetMapping("sjekk/{ansattId}/{brukerId}")
    @Operation(summary = SUMMARY_HAR, description = DESCRIPTION_HAR)
    fun harTilgang(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) =
        enkelt.harEnkeltTilgang(ansattId, brukerId)

    @GetMapping("gjeldende/{ansattId}/{brukerId}")
    @Operation(summary = SUMMARY_GJELDENDE, description = DESCRIPTION_GJELDENDE)
    fun gjeldende(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) =
        adapter.gjeldende(ansattId.verdi, brukerId.verdi, emptyList())

    companion object {
        private const val DEV_ENKELT_CONTROLLER_TAG_DESCRIPTION = "msg:openapi.dev.enkelt.tag.description"
        private const val DESCRIPTION_ENKELT = "msg:openapi.dev.enkelt.description"
        private const val SUMMARY_ENKELT = "msg:openapi.dev.enkelt.summary"
        private const val SUMMARY_HAR = "msg:openapi.dev.enkelt.har"
        private const val DESCRIPTION_HAR = "msg:openapi.dev.enkelt.har.description"
        private const val SUMMARY_GJELDENDE = "msg:openapi.dev.enkelt.gjeldende.summary"
        private const val DESCRIPTION_GJELDENDE = "msg:openapi.dev.enkelt.gjeldende.description"

    }
}
