    package no.nav.tilgangsmaskin.ansatt.vergemål

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

    @HttpExchange
interface VergemålClient {

    @PostExchange(VERGEMÅL_PATH)
    fun vergemål(@RequestBody body: VergemålIdent): List<Vergemål>

    @GetExchange(VERGEMÅL_PING_PATH)
    fun ping(): Any

        data class VergemålIdent(val ident: String)

        companion object {
        const val VERGEMÅL_PATH = "/api/v2/internbruker/vergemaal/kan-representere"
        const val VERGEMÅL_PING_PATH = "/actuator/health/liveness"
    }
}


