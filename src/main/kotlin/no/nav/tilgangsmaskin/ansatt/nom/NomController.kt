package no.nav.tilgangsmaskin.ansatt.nom

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.felles.rest.DevController
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@DevController(
    value = ["/${DEV}/nom"],
    name = "NomDevController")
class NomController(
    private val nom: NomTjeneste) {

    @GetMapping("/leder/{enhetId}")
    fun leder(@PathVariable enhetId: Enhetsnummer) =
        nom.lederForEnhet(enhetId)

    @GetMapping("/{ansattId}")
    fun nomFnr(@PathVariable ansattId: AnsattId) =
        nom.fnrForAnsatt(ansattId)
}