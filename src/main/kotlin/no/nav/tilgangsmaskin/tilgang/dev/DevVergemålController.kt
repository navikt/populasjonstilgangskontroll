package no.nav.tilgangsmaskin.tilgang.dev

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

private const val DEV_VERGEMAL_CONTROLLER_TAG_DESCRIPTION = "msg:openapi.dev.vergemal.tag.description"

@RestController
@RequestMapping("/${DEV}/skjermning")
@ConditionalOnNotProd
@Tag(name = "DevVergemålController", description = DEV_VERGEMAL_CONTROLLER_TAG_DESCRIPTION)
class DevVergemålController(private val vergemål: VergemålTjeneste) {


    @PostMapping("vergemål")
    @Operation(summary = SUMMARY_VERGEMAL, description = DESCRIPTION_VERGEMAL)
    fun vergemål(@RequestBody ansattId: AnsattId) =
        vergemål.alle(ansattId)

    companion object {
        private const val SUMMARY_VERGEMAL = "msg:openapi.dev.vergemal.vergemal.summary"
        private const val DESCRIPTION_VERGEMAL = "msg:openapi.dev.vergemal.vergemal.description"
    }
}
