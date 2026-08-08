package no.nav.tilgangsmaskin.ansatt.skjerming

import io.swagger.v3.oas.annotations.Operation
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingController.Companion.DEV_SKJERMING_CONTROLLER_TAG_DESCRIPTION
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody


@DevController(
    value = ["/${DEV}/skjerming"],
    name = "SkjermingController",
    description = DEV_SKJERMING_CONTROLLER_TAG_DESCRIPTION
)
class SkjermingController(private val skjerming: SkjermingTjeneste) {

    @PostMapping("skjerming")
    @Operation(summary = SUMMARY_SKJERMING, description = DESCRIPTION_SKJERMING)
    fun skjerming(@RequestBody brukerId: BrukerId) =
        skjerming.skjerming(brukerId)

    @PostMapping("skjerminger")
    @Operation(summary = SUMMARY_SKJERMINGER, description = DESCRIPTION_SKJERMINGER)
    fun skjerminger(@RequestBody ids: List<BrukerId>) =
        skjerming.skjerminger(ids)

    companion object {
        private const val DEV_SKJERMING_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.dev.skjerming.tag.description"
        private const val SUMMARY_SKJERMING = "${MSG}openapi.dev.skjerming.skjerming.summary"
        private const val DESCRIPTION_SKJERMING = "${MSG}openapi.dev.skjerming.skjerming.description"
        private const val SUMMARY_SKJERMINGER = "${MSG}openapi.dev.skjerming.skjerminger.summary"
        private const val DESCRIPTION_SKJERMINGER = "${MSG}openapi.dev.skjerming.skjerminger.description"
    }

}
