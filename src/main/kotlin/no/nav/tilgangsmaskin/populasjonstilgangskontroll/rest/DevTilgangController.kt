package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.RegelTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.DEV
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@UnprotectedRestController(value = ["/${DEV}"])
@ConditionalOnNotProd
class DevTilgangController(val bruker : BrukerTjeneste, private val ansatt: AnsattTjeneste, val regler: RegelTjeneste)
{
    @GetMapping("bruker")
    fun bruker(fnr: Fødselsnummer) = bruker.bruker(fnr)

    @GetMapping("ansatt")
    fun ansatt(navId: NavId) = ansatt.ansatt(navId)

    @GetMapping("regler")
    fun sjekkTilgang(@RequestParam ansattId: NavId, @RequestParam brukerId: Fødselsnummer) = regler.sjekkTilgang(ansattId, brukerId)

}