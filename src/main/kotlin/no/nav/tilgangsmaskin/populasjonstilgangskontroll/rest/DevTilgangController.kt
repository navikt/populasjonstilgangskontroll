package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomJPAAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelSpec
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringData
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.DEV
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@UnprotectedRestController(value = ["/${DEV}"])
@ConditionalOnNotProd
class DevTilgangController(private val bruker : BrukerTjeneste, private val ansatt: AnsattTjeneste, private val regler: RegelTjeneste, private val overstyring: OverstyringTjeneste, private val nom: NomJPAAdapter) {

    @GetMapping("bruker/{brukerId}")
    fun bruker(@PathVariable brukerId: BrukerId) = bruker.bruker(brukerId)

    @GetMapping("ansatt/{ansattId}")
    fun ansatt(@PathVariable ansattId: AnsattId) = ansatt.ansatt(ansattId)

    @PostMapping("ansatt/{ansattId}/{brukerId}")
    fun nom(@PathVariable ansattId: AnsattId,@PathVariable brukerId: BrukerId) = nom.upsert(ansattId.verdi,brukerId.verdi)

    @GetMapping("komplett/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    fun kompletteRegler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) = regler.kompletteRegler(ansattId, brukerId)

    @GetMapping("kjerne/{ansattId}/{brukerId}")
    @ResponseStatus(NO_CONTENT)
    fun kjerneregler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId)  = regler.kjerneregler(ansattId, brukerId)

    @PostMapping("overstyr/{ansattId}")
    @ResponseStatus(ACCEPTED)
    fun overstyr(@PathVariable ansattId: AnsattId, @RequestBody data: OverstyringData) = overstyring.overstyr(ansattId, data)

    @PostMapping("bulk/{ansattId}")
    @ResponseStatus(NO_CONTENT)
    fun bulk(@PathVariable ansattId: AnsattId,@RequestBody  specs: List<RegelSpec>) = regler.bulkRegler(ansattId, specs)

    @PostMapping("brukerbulk")
    fun brukerBulk(@RequestBody brukerIds: Array<String>) = bruker.brukerBulk(brukerIds.map { BrukerId(it) })}

/*
@UnprotectedRestController(value = ["/tmp"])
@ConditionalOnNotProd
class TempTilgangController(private val adapter: EntraClientAdapter, private val resolver: OIDResolver, ) {
    @GetMapping("ansatt/{ansattId}")
    fun ansatt(@PathVariable ansattId: AnsattId) : Sequence<EntraGrupperBolkAny> {
        val oid = resolver.oidForAnsatt(ansattId)
        return adapter.grupperRaw(oid.toString())
    }

 @JsonIgnoreProperties(ignoreUnknown = true)
data class EntraGrupperBolkAny(@JsonProperty("@odata.nextLink") val next: URI? = null, val value: Any)
}
*/