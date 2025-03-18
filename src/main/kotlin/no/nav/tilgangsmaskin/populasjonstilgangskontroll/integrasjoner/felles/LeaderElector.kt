package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient.Builder
import java.net.InetAddress
import java.net.URI
import java.time.LocalDateTime

@Service
class LeaderElector(private val adapter: LeaderElectorClientAdapter) {
    val erLeder get() = adapter.leder() == InetAddress.getLocalHost().hostName
}
@Component
class LeaderElectorClientAdapter(builder: Builder, cf : LeaderElectorConfig) : AbstractRestClientAdapter(builder.build(), cf) {
    fun leder() = get<LeaderElectorRespons>(cfg.baseUri).name
}
private data class LeaderElectorRespons(val name: String, val last_update: LocalDateTime)
@Component
class LeaderElectorConfig(@Value("\${elector.get.url}")  uri: URI): AbstractRestConfig(uri,"", isEnabled = true)
