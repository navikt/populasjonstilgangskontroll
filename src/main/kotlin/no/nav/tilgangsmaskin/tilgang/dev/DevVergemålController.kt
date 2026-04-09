package no.nav.tilgangsmaskin.tilgang.dev

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody


@UnprotectedRestController(value = ["/${DEV}/skjermning"])
@ConditionalOnNotProd
@Tag(name = "DevVergemålController", description = "Denne kontrolleren skal kun brukes til testing")
class DevVergemålController(private val vergemål: VergemålTjeneste){


    @PostMapping("vergemål")
    fun vergemål(@RequestBody ansattId: AnsattId) = vergemål.vergemål(ansattId)
}
