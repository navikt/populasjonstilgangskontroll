package no.nav.tilgangsmaskin.populasjonstilgangskontroll.Tilgang

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.Nom
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.bruker.pdl.PdlSyncGraphQLClientAdapter
import no.nav.tilgangsmaskin.felles.rest.ValidId
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import no.nav.tilgangsmaskin.regler.motor.IdOgType
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringData
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.RegelTjeneste
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*


@UnprotectedRestController(value = ["/${DEV}"])
@ConditionalOnNotProd
@Tag(name = "DevTilgangController", description = "Denne kontrolleren skal kun brukes til testing")
class DevTilgangController(
        private val graphql: PdlSyncGraphQLClientAdapter,
        private val skjerming: SkjermingTjeneste,
        private val brukere: BrukerTjeneste,
        private val ansatte: AnsattTjeneste,
        private val regler: RegelTjeneste,
        private val overstyring: OverstyringTjeneste,
        private val nom: Nom,
        private val pdl: PDLTjeneste) {

    @GetMapping("sivilstand/{id}")
    fun sivilstand(@PathVariable @Valid @ValidId id: String) = graphql.sivilstand(id)

    @GetMapping("bruker/{id}")
    fun bruker(@PathVariable @Valid @ValidId id: String) = brukere.utvidetFamilie(id)

    @GetMapping("person/{id}")
    fun person(@PathVariable @Valid @ValidId id: String) = pdl.utvidetFamile(id)

    @GetMapping("ansatt/{ansattId}")
    fun ansatt(@PathVariable ansattId: AnsattId) = ansatte.ansatt(ansattId)

    @PostMapping("ansatt/{ansattId}/{brukerId}")
    fun nom(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) =
        nom.lagre(NomAnsattData(ansattId, brukerId))

    @GetMapping("komplett/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    fun kompletteRegler(@PathVariable ansattId: AnsattId, @PathVariable @Valid @ValidId brukerId: String) =
        regler.kompletteRegler(ansattId, brukerId)

    @GetMapping("kjerne/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    fun kjerneregler(@PathVariable ansattId: AnsattId, @PathVariable @Valid @ValidId brukerId: String) =
        regler.kjerneregler(ansattId, brukerId)

    @PostMapping("overstyr/{ansattId}")
    @ResponseStatus(ACCEPTED)
    fun overstyr(@PathVariable ansattId: AnsattId, @RequestBody data: OverstyringData) =
        overstyring.overstyr(ansattId, data)

    @PostMapping("bulk/{ansattId}")
    @ResponseStatus(NO_CONTENT)
    fun bulkregler(@PathVariable ansattId: AnsattId, @RequestBody @Valid @ValidId specs: Set<IdOgType>) =
        regler.bulkRegler(ansattId, specs)

    @PostMapping("skjerming")
    fun skjerming(@RequestBody brukerId: BrukerId) = skjerming.skjerming(brukerId)

    @PostMapping("skjerminger")
    fun skjerminger(@RequestBody ids: List<BrukerId>) = skjerming.skjerminger(ids)

    @PostMapping("brukere")
    fun brukere(@RequestBody @Valid @ValidId ids: Set<String>) = brukere.brukere(ids)
}
