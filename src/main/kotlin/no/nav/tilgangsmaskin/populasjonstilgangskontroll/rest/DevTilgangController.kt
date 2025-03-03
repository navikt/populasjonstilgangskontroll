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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@UnprotectedRestController(value = ["/${DEV}"])
@ConditionalOnNotProd
class DevTilgangController(private val bruker : BrukerTjeneste, private val ansatt: AnsattTjeneste, private val regler: RegelTjeneste, private val overstyringTjeneste: OverstyringTjeneste)
{
    @GetMapping("bruker")
    fun bruker(fnr: BrukerId) = bruker.bruker(fnr)

    @PostMapping("brukere")
    fun brukere(@RequestBody brukerIds: List<BrukerId>) = bruker.bolk(brukerIds)

    @GetMapping("pip")
    fun pipbruker(fnr: BrukerId) = bruker.brukerPip(fnr)

    @GetMapping("ansatt")
    fun ansatt(ansattId: AnsattId) = ansatt.ansatt(ansattId)

    @GetMapping("komplett")
    fun kompletteRegler(@RequestParam ansattId: AnsattId, @RequestParam brukerId: BrukerId) : ResponseEntity<Unit> {
        regler.kompletteRegler(ansattId, brukerId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("kjerne")
    fun kjerneregler(@RequestParam ansattId: AnsattId, @RequestParam brukerId: BrukerId) : ResponseEntity<Unit> {
        regler.kjerneregler(ansattId, brukerId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("overstyr/{ansattId}")
    fun overstyr(@PathVariable ansattId: AnsattId, @RequestBody data: OverstyringData): ResponseEntity<Unit> {
        overstyringTjeneste.overstyr(ansattId, data)
        return ResponseEntity.accepted().build()
    }


    @PostMapping("bulk/{ansattId}/")
    fun bulk(@PathVariable ansattId: AnsattId,@RequestBody vararg specs: RegelSpec): ResponseEntity<Unit> {
        regler.bulkRegler(ansattId, *specs)
        return ResponseEntity.noContent().build()
    }
}