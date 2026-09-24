package no.nav.tilgangsmaskin.ansatt.nom

import io.swagger.v3.oas.annotations.Operation
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.tilgang.openapi.MSG
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable



@DevController(
    value = ["/${DEV}/nom/"],
    name = "NomController")
class NomController(
    private val nom: NomTjeneste) {

    @GetMapping("{ansattId}")
    fun leder(@PathVariable ansattId: AnsattId) = nom.lederForAnsatt(ansattId)
}