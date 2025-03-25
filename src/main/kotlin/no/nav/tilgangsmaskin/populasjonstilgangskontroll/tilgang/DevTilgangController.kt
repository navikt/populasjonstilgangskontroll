package no.nav.tilgangsmaskin.populasjonstilgangskontroll.Tilgang

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomAnsattData
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringData
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.IdOgType
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.cluster.ClusterConstants.DEV
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*


@UnprotectedRestController(value = ["/${DEV}"])
@ConditionalOnNotProd
class DevTilgangController(private val pdl: PDLTjeneste,private val bruker : BrukerTjeneste, private val ansatt: AnsattTjeneste, private val regler: RegelTjeneste, private val overstyring: OverstyringTjeneste, private val nom: NomTjeneste) {

    @GetMapping("bruker/{brukerId}")
    fun bruker(@PathVariable brukerId: BrukerId) = bruker.bruker(brukerId)

    @GetMapping("ansatt/{ansattId}")
    fun ansatt(@PathVariable ansattId: AnsattId) = ansatt.ansatt(ansattId)

    @PostMapping("ansatt/{ansattId}/{brukerId}")
    fun nom(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) = nom.lagre(NomAnsattData(ansattId, brukerId))

    @GetMapping("komplett/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    fun kompletteRegler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) = regler.kompletteRegler(ansattId, brukerId)

    @GetMapping("kjerne/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    fun kjerneregler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId)  = regler.kjerneregler(ansattId, brukerId)

    @PostMapping("overstyr/{ansattId}")
    @ResponseStatus(ACCEPTED)
    fun overstyr(@PathVariable ansattId: AnsattId, @RequestBody data: OverstyringData) = overstyring.overstyr(ansattId, data)

    @PostMapping("brukere1")
    fun personer(@RequestBody  brukerIds: List<BrukerId>) = pdl.personer1(brukerIds)

    @PostMapping("bulk/{ansattId}")
    @ResponseStatus(NO_CONTENT)
    fun bulkregler(@PathVariable ansattId: AnsattId, @RequestBody  specs: List<IdOgType>) = regler.bulkRegler(ansattId, specs)

    @PostMapping("brukere")
    fun brukere(@RequestBody brukerIds: List<BrukerId>) = bruker.brukere(brukerIds)
}
