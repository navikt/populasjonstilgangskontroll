package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringMetadata
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
    fun bruker(fnr: Fødselsnummer) = bruker.bruker(fnr)

    @GetMapping("ansatt")
    fun ansatt(navId: NavId) = ansatt.ansatt(navId)

    @GetMapping("regler")
    fun alleRegler(@RequestParam ansattId: NavId, @RequestParam brukerId: Fødselsnummer) = regler.alleRegler(ansattId, brukerId)

    @GetMapping("kjerneregler")
    fun kjerneregler(@RequestParam ansattId: NavId, @RequestParam brukerId: Fødselsnummer) = regler.kjerneregler(ansattId, brukerId)

    @PostMapping("overstyr/{ansattId}/{brukerId}")
    fun overstyr(@PathVariable ansattId: NavId, @PathVariable brukerId: Fødselsnummer, @RequestBody metadata: OverstyringMetadata): ResponseEntity<Unit> {
        overstyringTjeneste.overstyr(ansattId, brukerId, metadata)
        return ResponseEntity.accepted().build()
    }
}