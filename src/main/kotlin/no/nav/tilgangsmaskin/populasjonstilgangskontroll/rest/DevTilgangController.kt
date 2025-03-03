package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelSpec
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringData
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.DEV
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@UnprotectedRestController(value = ["/${DEV}"])
@ConditionalOnNotProd
class DevTilgangController(private val bruker : BrukerTjeneste, private val ansatt: AnsattTjeneste, private val regler: RegelTjeneste, private val overstyringTjeneste: OverstyringTjeneste)
{
    @GetMapping("bruker/{brukerId}")
    fun bruker(@PathVariable brukerId: BrukerId) = bruker.bruker(brukerId)

    @GetMapping("pip/{brukerId}")
    fun pipbruker(@PathVariable brukerId: BrukerId) = bruker.brukerPip(brukerId)

    @GetMapping("ansatt/{ansattId}")
    fun ansatt(@PathVariable ansattId: AnsattId) = ansatt.ansatt(ansattId)

    @GetMapping("komplett/{ansattId}/{brukerId}")
    fun kompletteRegler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) : ResponseEntity<Unit> {
        regler.kompletteRegler(ansattId, brukerId)
        return noContent().build()
    }

    @GetMapping("kjerne/{ansattId}//{brukerId}")
    fun kjerneregler(@PathVariable ansattId: AnsattId, @PathVariable brukerId: BrukerId) : ResponseEntity<Unit> {
        regler.kjerneregler(ansattId, brukerId)
        return noContent().build()
    }

    @PostMapping("overstyr/{ansattId}")
    fun overstyr(@PathVariable ansattId: AnsattId, @RequestBody data: OverstyringData): ResponseEntity<Unit> {
        overstyringTjeneste.overstyr(ansattId, data)
        return accepted().build()
    }

    @PostMapping("bulk/{ansattId}/")
    fun bulk(@PathVariable ansattId: AnsattId,@RequestBody vararg specs: RegelSpec): ResponseEntity<Unit> {
        regler.bulkRegler(ansattId, *specs)
        return noContent().build()
    }
}