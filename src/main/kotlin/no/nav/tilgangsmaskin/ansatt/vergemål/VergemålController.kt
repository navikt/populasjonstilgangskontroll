package no.nav.tilgangsmaskin.ansatt.vergemål

import io.swagger.v3.oas.annotations.Operation
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålController.Companion.DEV_VERGEMAL_CONTROLLER_TAG_DESCRIPTION
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody


@DevController(
    value = ["/${DEV}/skjermning"],
    name = "VergemålController",
    description = DEV_VERGEMAL_CONTROLLER_TAG_DESCRIPTION
)
class VergemålController(private val vergemål: VergemålTjeneste) {


    @PostMapping("vergemål")
    @Operation(summary = SUMMARY_VERGEMAL, description = DESCRIPTION_VERGEMAL)
    fun vergemål(@RequestBody ansattId: AnsattId) =
        vergemål.alle(ansattId)

    companion object {
        private const val DEV_VERGEMAL_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.dev.vergemal.tag.description"
        private const val SUMMARY_VERGEMAL = "${MSG}openapi.dev.vergemal.vergemal.summary"
        private const val DESCRIPTION_VERGEMAL = "${MSG}openapi.dev.vergemal.vergemal.description"
    }
}
