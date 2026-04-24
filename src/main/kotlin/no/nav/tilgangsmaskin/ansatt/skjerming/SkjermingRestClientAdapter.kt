package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.felles.rest.Pingable
import org.springframework.stereotype.Component

@Component
class SkjermingRestClientAdapter(private val client: SkjermingClient, private val cfg: SkjermingConfig) : Pingable {

    override val name = cfg.name
    override val pingEndpoint = "${cfg.pingEndpoint}"

    override fun ping() =
        client.ping()

    fun skjerming(id: String) =
        client.skjerming(mapOf(IDENT to id))

    fun skjerminger(ids: Set<String>) =
        if (ids.isEmpty()) emptyMap()
        else client.skjerminger(mapOf(IDENTER to ids))

    private companion object  {
        private const val IDENT = "personident"
        private const val IDENTER = IDENT + "er"
    }
}
