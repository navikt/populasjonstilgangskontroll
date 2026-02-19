package no.nav.tilgangsmaskin.tilgang.dev

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.bruker.pdl.PdlTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.PdlRestClientAdapter
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@UnprotectedRestController(value = ["/${ClusterConstants.DEV}/bruker/"])
@ConditionalOnNotProd
@Tag(name = "DevBrukerController", description = "Denne kontrolleren skal kun brukes til testing")
class DevBrukerController(private val brukere: BrukerTjeneste,
                          private val pdl: PdlTjeneste,
                          private val pip: PdlRestClientAdapter) {

    @GetMapping("person/pip/{id}")
    fun pip(@PathVariable id: String) = pip.person(id)


    @GetMapping("person/{id}")
    fun person(@PathVariable id: String) = pdl.medUtvidetFamile(id)

    @PostMapping("brukere")
    fun brukere(@RequestBody ids: Set<String>) = brukere.brukere(ids)

    @PostMapping("brukeridentifikator")
    fun brukerIdentifikator(@RequestBody id: Identifikator) = brukere.brukerMedUtvidetFamilie(id.verdi)

    @GetMapping("bruker/{id}")
    fun bruker(@PathVariable id: String) = brukere.brukerMedUtvidetFamilie(id)


}