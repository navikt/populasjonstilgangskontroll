package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.sikkerhetstjenesten.felles.domain.AnsattId
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.core.KafkaOperations
import org.springframework.stereotype.Component
import java.lang.Thread.currentThread
import java.util.concurrent.TimeUnit.SECONDS

@Component
class EnkeltTilgangHendelseProdusent(private val kafka: KafkaOperations<String, Any>) {
    private val log = getLogger(javaClass)

    fun publiser(ansattId: AnsattId) {
        runCatching {
            log.info("Publiserer hendelse om ny enkelttilgang for $ansattId")
            kafka.sendDefault(ansattId.verdi, ansattId).get(10, SECONDS)
        }.onSuccess {
            log.info("Publiserte hendelse om ny enkelttilgang for {}", ansattId)
        }.onFailure { e ->
            if (e is InterruptedException) currentThread().interrupt()
            log.error("Kunne ikke publisere hendelse om ny enkelttilgang for {}", ansattId, e)
        }
    }
}
