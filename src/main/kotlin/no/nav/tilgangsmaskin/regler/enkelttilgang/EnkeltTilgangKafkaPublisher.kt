package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.tilgangsmaskin.ansatt.AnsattId
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.core.KafkaOperations
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit.SECONDS

@Component
class EnkeltTilgangKafkaPublisher(private val kafka: KafkaOperations<String, Any>) {
    private val log = getLogger(javaClass)

    fun publiser(ansattId: AnsattId) {
        runCatching {
            kafka.sendDefault(ansattId.verdi, ansattId).get(10, SECONDS)
        }.onSuccess {
            log.info("Publiserte enkelttilgang til Kafka for ansatt {}", ansattId)
        }.onFailure { e ->
            if (e is InterruptedException) Thread.currentThread().interrupt()
            log.error("Kunne ikke publisere enkelttilgang til Kafka", e)
        }
    }
}
