package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PersonTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangsService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLConfig.Companion.PDL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import java.util.UUID

@ProtectedRestController(value = ["/api/v1"], issuer = "azuread", claimMap = [])
class Tilgangskontroll(val service : TilgangsService) {

    @PostMapping("sjekkTilgang")
    fun sjekkTilgang(bruker_ident: String): TilgangsResponse{
        //valider token.
        val nav_ident = "N999999" // Hent fra token
                return TilgangsResponse(
                    bruker_ident = bruker_ident,
                    nav_ident = "N999999",
                    ansatt_har_tilgang = true,
                    begrunnelse = Begrunnelse(begrunnelse= "Begrunnelse", begrunnelse_kode = "", kan_overstyres = false) //optional
                )
            }

}

data class TilgangsResponse(
    val bruker_ident: String,
    val nav_ident: String,
    val ansatt_har_tilgang: Boolean,
    val begrunnelse: Begrunnelse? = null,

)
data class Begrunnelse(
    val begrunnelse: String,
    val begrunnelse_kode: String,
    val kan_overstyres: Boolean
)

@UnprotectedRestController(value = ["/dev"])
@ConditionalOnNotProd
class DevController(val pdl : PersonTjeneste, val skjerming: SkjermingTjeneste, val ansatt: AnsattTjeneste )
{
    @GetMapping(PDL)
    fun hentPerson(fnr: Fødselsnummer) = pdl.hentPerson(fnr)

    @GetMapping(SKJERMING)
    fun erSkjermet(fnr: Fødselsnummer) = skjerming.erSkjermet(fnr)

    @GetMapping("ansatt")
    fun hentAnsatt(ident: NavId) = ansatt.ansattAzureId(ident)

    @GetMapping("ansattilganger")
    fun hentAnsattTilganger(azureId : UUID) = ansatt.ansattTilganger(azureId)

}
