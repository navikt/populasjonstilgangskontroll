package no.nav.tilgangsmaskin.ansatt.skjerming

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@HttpExchange
interface SkjermingClient {

    @PostExchange(SKJERMING_PATH)
    fun skjerming(@RequestBody body: Map<String, String>): Boolean

    @PostExchange(SKJERMING_BULK_PATH)
    fun skjerminger(@RequestBody body: Map<String, Set<String>>): Map<String, Boolean>

    @GetExchange(PING_PATH)
    fun ping(): Any

    companion object    {
        const val SKJERMING_PATH = "skjermet"
        const val SKJERMING_BULK_PATH = "skjermetBulk"
        const val PING_PATH = "internal/health/liveness"
    }
}

