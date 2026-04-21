package no.nav.tilgangsmaskin.ansatt.vergemål

import no.nav.tilgangsmaskin.felles.rest.Pingable
import org.springframework.stereotype.Component

@Component
class VergemålRestClientAdapter(private val client: VergemålClient, private val cfg: VergemålConfig) : Pingable {

    override val name = cfg.name
    override val pingEndpoint = "${cfg.pingEndpoint}"
    override val isEnabled = cfg.isEnabled
    override fun ping() = if (cfg.isEnabled) client.ping() else "disabled"

    fun vergemål(ident: String) =
        client.vergemål(VergemålIdent(ident))
            .mapTo(mutableSetOf()) { it.vergehaver }
            .toSet()

}