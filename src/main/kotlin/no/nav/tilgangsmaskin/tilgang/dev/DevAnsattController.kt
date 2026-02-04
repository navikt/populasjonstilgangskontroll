package no.nav.tilgangsmaskin.populasjonstilgangskontroll.Tilgang

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjeneste
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


@UnprotectedRestController(value = ["/${DEV}/ansatt/"])
@ConditionalOnNotProd
@Tag(name = "DevAnsattController", description = "Denne kontrolleren skal kun brukes til testing")
class DevAnsattController(
    private val ansatte: AnsattTjeneste,
    private val proxy: EntraProxyTjeneste) {
    
    @GetMapping("{ansattId}")
    fun ansatt(@PathVariable ansattId: AnsattId) = ansatte.ansatt(ansattId)

    @GetMapping("proxy/{ansattId}")
    fun enhet(@PathVariable ansattId: AnsattId) = proxy.enhet(ansattId)

    @GetMapping("enheter/{ansattId}")
    fun enheter(@PathVariable ansattId: AnsattId) = proxy.enheter(ansattId)

}