package no.nav.tilgangsmaskin.populasjonstilgangskontroll.Tilgang

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import no.nav.tilgangsmaskin.tilgang.BulkSwaggerApiRespons
import no.nav.tilgangsmaskin.tilgang.ProblemDetailApiResponse
import no.nav.tilgangsmaskin.tilgang.RegelTjeneste
import org.springframework.http.HttpStatus.MULTI_STATUS
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/${DEV}/regel/")
@ConditionalOnNotProd
@Tag(name = "DevRegelController", description = "Denne kontrolleren skal kun brukes til testing")
class DevRegelController(private val regler: RegelTjeneste) {
    @GetMapping("komplett/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    fun kompletteRegler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: String) =
        regler.kompletteRegler(ansattId, brukerId.trim('"'))

    @GetMapping("kjerne/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    @ProblemDetailApiResponse
    fun kjerneregler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: String) =
        regler.kjerneregler(ansattId, brukerId.trim('"'))
    @PostMapping("bulk/{ansattId}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    fun bulkregler(@PathVariable ansattId: AnsattId, @RequestBody specs: Set<BrukerIdOgRegelsett>) =
        regler.bulkRegler( ansattId, specs)

    @PostMapping("bulk/{ansattId}/{regelType}")
    @ResponseStatus(MULTI_STATUS)
    @BulkSwaggerApiRespons
    fun bulkreglerForRegelType(@PathVariable ansattId: AnsattId, @PathVariable regelType: RegelType, @RequestBody brukerIds: Set<BrukerId>) =
        regler.bulkRegler(ansattId, brukerIds.map { BrukerIdOgRegelsett(it.verdi, regelType) }.toSet())


}