package no.nav.tilgangsmaskin.populasjonstilgangskontroll.rest

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.ProtectedRestController
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl.PDLService
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingRestClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

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
class DevController(val service : PDLService, val skjermingRestClientAdapter: SkjermingRestClientAdapter)
{
    @GetMapping("pdl")
    fun hentPerson(fnr: Fødselsnummer) = service.hentPerson(fnr)

    @GetMapping("skjermet")
    fun hentSkjermet(fnr: Fødselsnummer) = skjermingRestClientAdapter.skjermetPerson(fnr)

}
