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
import java.util.concurrent.TimeUnit.SECONDS


@Component
class NomVaktmester(private val nom: NomTjeneste, private val elector: LeaderElector) {

    private val log = getLogger(NomVaktmester::class.java)

    @Scheduled(fixedRate = 60, timeUnit = SECONDS)
    fun fjern() {
        if ((elector.erLeder())) {
            log.info("Vaktmester fjerner gamle data")
            nom.ryddOpp()
        }
        else {
            log.info("Vaktmester er ikke leder")
        }
    }
}

@Service
class LeaderElector(private val client: LeaderElectionClientAdapter) {
    fun erLeder() = client.lederHostname() == InetAddress.getLocalHost().hostName
}
@Component
class LeaderElectionClientAdapter(@Qualifier(NOM) client: RestClient, private val cf : LeaderElectionConfig, errorHandler: ErrorHandler) : AbstractRestClientAdapter(client, cf, errorHandler) {
    fun lederHostname() = get<LeaderElectionResponse>(cf.baseUri).name
}
data class LeaderElectionResponse(val name: String, val last_update: LocalDateTime)
@Component
class LeaderElectionConfig(@Value("\${elector.get.url}")  uri: URI): AbstractRestConfig(uri,"", isEnabled =true)