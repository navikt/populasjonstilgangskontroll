package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.felles.rest.AbstractPingable
import org.springframework.stereotype.Component

@Component
class SkjermingRestClientAdapter(private val client: SkjermingClient, cfg: SkjermingConfig) : AbstractPingable(cfg, client::ping) {

    fun skjerming(id: String) =
        client.skjerming(mapOf(IDENT to id))

    fun skjerminger(ids: Set<String>) =
        if (ids.isEmpty()) emptyMap()
        else client.skjerminger(mapOf(IDENTER to ids))

    private companion object {
        private const val IDENT = "personident"
        private const val IDENTER = IDENT + "er"
    }
}
