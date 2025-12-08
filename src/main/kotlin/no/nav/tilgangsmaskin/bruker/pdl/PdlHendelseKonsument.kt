package no.nav.tilgangsmaskin.bruker.pdl


import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlRestClientAdapter.Companion.PDL_CACHES
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PdlHendelseKonsument(private val client: CacheClient) {
    private val log = getLogger(javaClass)

    @KafkaListener(topics = [ "pdl.leesah-v1"], containerFactory = "pdlAvroListenerContainerFactory", filter = "graderingFilterStrategy")
    fun listen(hendelse: Personhendelse) {
        log.info("Mottok hendelse fra PDL $hendelse, tømmer caches" )
        PDL_CACHES.forEach { cache ->
            hendelse.personidenter.forEach { id ->
              client.delete(cache, id)
                log.trace(CONFIDENTIAL, "Slettet ident {} etter hendelse: {}", id, hendelse)
            }
        }
    }
}

