package no.nav.tilgangsmaskin.ansatt.vergemål

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@HttpExchange
interface VergemålClient {

    @PostExchange(VERGEMÅL_PATH)
    fun vergemål(@RequestBody body: VergemålIdent): Set<Vergemål>

    @GetExchange(PING_PATH)
    fun ping(): Any

    companion object {
        const val VERGEMÅL_PATH = "/api/v2/internbruker/vergemaal/kan-representere"
        const val PING_PATH = "/actuator/health/liveness"
    }
}

data class VergemålIdent(val ident: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Vergemål(val vergehaver: BrukerId)