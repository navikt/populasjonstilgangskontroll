package no.nav.tilgangsmaskin.ansatt

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import no.nav.tilgangsmaskin.ansatt.DevAnsattController.Companion.DEV_ANSATT_CONTROLLER_TAG_DESCRIPTION
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


@UnprotectedRestController(value = ["/${DEV}/ansatt/"])
@ConditionalOnNotProd
@Tag(name = "DevAnsattController", description = DEV_ANSATT_CONTROLLER_TAG_DESCRIPTION)
class DevAnsattController(
    private val ansatte: AnsattTjeneste,
    private val proxy: EntraProxyTjeneste) {

    @GetMapping("{ansattId}")
    @Operation(summary = SUMMARY_ANSATT, description = DESCRIPTION_ANSATT)
    fun ansatt(@PathVariable ansattId: AnsattId) = ansatte.ansatt(ansattId)

    @GetMapping("proxy/{ansattId}")
    @Operation(summary = SUMMARY_PROXY_ENHET, description = DESCRIPTION_PROXY_ENHET)
    fun enhet(@PathVariable ansattId: AnsattId) = proxy.enhet(ansattId)

    @GetMapping("enheter/{ansattId}")
    @Operation(summary = SUMMARY_PROXY_ENHETER, description = DESCRIPTION_PROXY_ENHETER)
    fun enheter(@PathVariable ansattId: AnsattId) = proxy.enheter(ansattId)

    companion object {
        private const val DEV_ANSATT_CONTROLLER_TAG_DESCRIPTION = "${MSG}openapi.dev.ansatt.tag.description"

        private const val SUMMARY_ANSATT = "${MSG}openapi.dev.ansatt.ansatt.summary"
        private const val DESCRIPTION_ANSATT = "${MSG}openapi.dev.ansatt.ansatt.description"
        private const val SUMMARY_PROXY_ENHET = "${MSG}openapi.dev.ansatt.proxy.enhet.summary"
        private const val DESCRIPTION_PROXY_ENHET = "${MSG}openapi.dev.ansatt.proxy.enhet.description"
        private const val SUMMARY_PROXY_ENHETER = "${MSG}openapi.dev.ansatt.proxy.enheter.summary"
        private const val DESCRIPTION_PROXY_ENHETER = "${MSG}openapi.dev.ansatt.proxy.enheter.description"
    }

}