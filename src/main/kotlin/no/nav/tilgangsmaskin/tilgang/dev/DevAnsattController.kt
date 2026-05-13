package no.nav.tilgangsmaskin.tilgang.dev

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

private const val DEV_ANSATT_CONTROLLER_TAG_DESCRIPTION = "msg:openapi.dev.ansatt.tag.description"

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
        private const val SUMMARY_ANSATT = "msg:openapi.dev.ansatt.ansatt.summary"
        private const val DESCRIPTION_ANSATT = "msg:openapi.dev.ansatt.ansatt.description"
        private const val SUMMARY_PROXY_ENHET = "msg:openapi.dev.ansatt.proxy.enhet.summary"
        private const val DESCRIPTION_PROXY_ENHET = "msg:openapi.dev.ansatt.proxy.enhet.description"
        private const val SUMMARY_PROXY_ENHETER = "msg:openapi.dev.ansatt.proxy.enheter.summary"
        private const val DESCRIPTION_PROXY_ENHETER = "msg:openapi.dev.ansatt.proxy.enheter.description"
    }

}