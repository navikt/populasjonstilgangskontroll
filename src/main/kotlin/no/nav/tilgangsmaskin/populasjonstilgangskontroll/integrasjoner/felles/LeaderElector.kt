package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomConfig.Companion.NOM
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.net.InetAddress
import java.net.URI
import java.time.LocalDateTime

@Service
class LeaderElector(private val adapter: LeaderElectorClientAdapter) {
    val erLeder get() = adapter.lederHostname() == InetAddress.getLocalHost().hostName
}
@Component
class LeaderElectorClientAdapter(@Qualifier(NOM) client: RestClient, cf : LeaderElectorConfig) : AbstractRestClientAdapter(client, cf) {
    fun lederHostname() = get<LeaderElectorRespons>(cfg.baseUri).name
}
private data class LeaderElectorRespons(val name: String, val last_update: LocalDateTime)
@Component
class LeaderElectorConfig(@Value("\${elector.get.url}")  uri: URI): AbstractRestConfig(uri,"", isEnabled = true)
