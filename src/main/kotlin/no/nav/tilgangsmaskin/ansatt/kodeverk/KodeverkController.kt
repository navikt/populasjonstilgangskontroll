package no.nav.tilgangsmaskin.ansatt.kodeverk

import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.web.bind.annotation.GetMapping

@DevController(
    value = ["/${DEV}/kodeverk"],
    name = "KodeverkController")
class KodeverkController(
    private val kodeverk: KodeverkTjeneste) {

    @GetMapping("/kommuner")
    fun kommuneBetydninger() =
        kodeverk.kommuner()

    @GetMapping("/bydeler")
    fun bydelerBetydninger() =
        kodeverk.bydeler()
}