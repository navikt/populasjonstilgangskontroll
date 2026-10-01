package no.nav.tilgangsmaskin.ansatt.kodeverk

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@DevController(
    value = ["/${DEV}/kodeverk"],
    name = "KodeverkController")
class KodeverkController(
    private val kodeverk: KodeverkTjeneste) {

    @GetMapping("/bydeler")
    fun bydeler() =
        kodeverk.bydeler()

    @GetMapping("/kommuner")
    fun kommuner() =
        kodeverk.kommuner()

    @GetMapping("/kommuner/betydninger")
    fun kommuneBetydninger() =
        kodeverk.kommuneBetydninger()

    @GetMapping("/bydeler/betydninger")
    fun bydelerBetydninger() =
        kodeverk.bydelBetydninger()

}