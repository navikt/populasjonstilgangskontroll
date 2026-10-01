package no.nav.tilgangsmaskin.ansatt.kodeverk

import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.http.MediaType.TEXT_PLAIN_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@DevController(
    value = ["/${DEV}/kodeverk"],
    name = "KodeverkController")
class KodeverkController(
    private val kodeverk: KodeverkTjeneste) {

    @GetMapping("/navn", produces = [TEXT_PLAIN_VALUE])
    fun navn(@RequestParam kode: String) =
        kodeverk.navn(kode)
}