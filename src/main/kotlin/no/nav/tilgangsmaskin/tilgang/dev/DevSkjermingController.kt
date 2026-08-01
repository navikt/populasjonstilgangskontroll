package no.nav.tilgangsmaskin.tilgang.dev

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.MSG
import no.nav.tilgangsmaskin.tilgang.dev.DevSkjermingController.Companion.DEV_SKJERMING_CONTROLLER_TAG_DESCRIPTION
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/${DEV}/skjerming")
@ConditionalOnNotProd
@Tag(name = "DevSkjermingController", description = DEV_SKJERMING_CONTROLLER_TAG_DESCRIPTION)
class DevSkjermingController(private val skjerming: SkjermingTjeneste) {

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
