package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Norsk_Ident
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenUtil
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono


@Component
class PdlGraphClient(
    private val webClient: WebClient,
    private val tokenUtil: TokenUtil) {


    fun hentPerson(norskIdent: Norsk_Ident):PdlGraphResponse {
        var results = PdlGraphResponse(null, null)

        runCatching {

                results = webClient.post()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenUtil.getAppAccessTokenWithPdlScope()}")
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header("behandlingsnummer", "B897") // Behandlingsnummer: B897 Hjemmel for å hente personopplysninger
                    .bodyValue(hentPersonQuery(norskIdent))
                    .retrieve()
                    .bodyToMono<PdlGraphResponse>()
                    .block() ?: throw RuntimeException("Person not found")

            }
        .onFailure {
            //logg til securelog
            throw RuntimeException("PDL could not be reached")
        }

        return results
    }

    fun hentPersoner(norskIdenter: List<Norsk_Ident>):List<Person> {
    return emptyList() // TODO implementere bulk henting av personer
    }

}