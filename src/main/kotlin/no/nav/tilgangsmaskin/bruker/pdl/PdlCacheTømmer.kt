package no.nav.tilgangsmaskin.bruker.pdl


import no.nav.person.pdl.leesah.Personhendelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlRestClientAdapter.Companion.PDL_CACHES
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PdlCacheTømmer(private val client: CacheClient) {
    private val log = getLogger(javaClass)

    @KafkaListener(topics = [ "pdl.leesah-v1"], containerFactory = "pdlAvroListenerContainerFactory", filter = "graderingFilterStrategy")
    fun listen(hendelse: Personhendelse) {
        log.info("Mottok hendelse av tyoe ${hendelse.adressebeskyttelse?.gradering?.name} fra PDL, tømmer cacher" )
        PDL_CACHES.forEach { cache ->
            hendelse.personidenter.forEach { id ->
                if (client.delete(cache, id) > 0) {
                    log.trace( "Slettet nøkkel ${client.tilNøkkel(cache, id)} fra cache ${cache.name} etter hendelse av type: {}", id.maskFnr(), hendelse.adressebeskyttelse?.gradering?.name)
                }
                else {
                    log.trace( "Fant ikke ident {} i ${cache.name} for sletting ved hendelse av type: {}", id.maskFnr(), hendelse.adressebeskyttelse?.gradering?.name)
                }
            }
        }
    }
}

