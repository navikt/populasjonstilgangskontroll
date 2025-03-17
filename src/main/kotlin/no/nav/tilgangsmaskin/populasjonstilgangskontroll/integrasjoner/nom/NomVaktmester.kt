package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.AbstractRestConfig
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomConfig.Companion.NOM
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler
import java.net.InetAddress
import java.net.URI
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit.MINUTES

@Component
class NomVaktmester(private val nom: NomTjeneste, private val elector: LeaderElector) {

    private val log = getLogger(NomVaktmester::class.java)

    @Scheduled(fixedRate = 60, timeUnit = MINUTES)
    fun ryddOpp() {
        if ((elector.erLeder)) {
            log.info("Vaktmester fjerner utgått informasjon")
            nom.ryddOpp().also {
                log.info("Vaktmester fjernet $it rad(er) med utgått informasjon")
            }
        }
        else {
            log.info("Vaktmester er ikke leder")
        }
    }
}

@Service
class LeaderElector(private val client: LeaderElectorClientAdapter) {
    val erLeder get() = client.lederHostname() == InetAddress.getLocalHost().hostName
}
@Component
class LeaderElectorClientAdapter(@Qualifier(NOM) client: RestClient, private val cf : LeaderElectorConfig, errorHandler: ErrorHandler) : AbstractRestClientAdapter(client, cf, errorHandler) {
    fun lederHostname() = get<LeaderElectorRespons>(cf.baseUri).name
}
private data class LeaderElectorRespons(val name: String, val last_update: LocalDateTime)
@Component
class LeaderElectorConfig(@Value("\${elector.get.url}")  uri: URI): AbstractRestConfig(uri,"", isEnabled = true)


/*
@Service
class SseService(private val webClient: WebClient.Builder) {

    fun subscribeToSse(url: String): Flux<String> {
        return webClient.build()
            .get()
            .uri(url)
            .retrieve()
            .bodyToFlux(String::class.java)
    }
}*/