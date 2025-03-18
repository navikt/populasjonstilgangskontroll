package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient.Builder
import java.net.InetAddress
import java.net.URI
import java.time.LocalDateTime

@Service
class LederUtvelger(private val adapter: LederUtvelgerClientAdapter) {
    val erLeder get() = adapter.leder() == InetAddress.getLocalHost().hostName
}
@Component
class LederUtvelgerClientAdapter(builder: Builder, cf : LederUtvelgerConfig) : AbstractRestClientAdapter(builder.build(), cf) {
    fun leder() = get<LederUtvelgerRespons>(cfg.baseUri).name
}
private data class LederUtvelgerRespons(val name: String, val last_update: LocalDateTime)
@Component
class LederUtvelgerConfig(@Value("\${elector.get.url}")  base: URI): AbstractRestConfig(base)
