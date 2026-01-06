package no.nav.tilgangsmaskin.bruker.pdl


import io.micrometer.core.instrument.Tags
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.UGRADERT
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_CACHES
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.PdlCacheTømmerTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.Locale.*

@Component
class PdlCacheOpprydder(private val teller: PdlCacheTømmerTeller, private val client: CacheClient, private val pdl: PDLTjeneste) {
    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = [ "pdl.leesah-v1"],
        containerFactory = "pdlAvroListenerContainerFactory",
        filter = "graderingFilterStrategy")
    fun listen(hendelse: Personhendelse) {
        val gradering = (hendelse.adressebeskyttelse?.gradering ?: UGRADERT).name
        val type = hendelse.endringstype?.name ?: "N/A"
        PDL_CACHES.forEach { cache ->
            hendelse.personidenter.forEach { id ->
                slett(cache, id, gradering, type)
            }
            refresh(hendelse.personidenter, gradering)
        }
    }

    private fun slett(cache: CachableConfig, id: String, gradering: String, type: String) {
        if (client.delete(id,cache) > 0) {
            teller.tell(Tags.of("cache", cache.name, "gradering",
                gradering.lowercase(getDefault()),"type",type))
            log.trace(CONFIDENTIAL,"Slettet nøkkel ${client.tilNøkkel(cache, id)} fra cache ${cache.name} etter hendelse av type: {}", id.maskFnr(), gradering)
            log.info("Slettet innslag fra cache ${cache.name} etter hendelse med gradering: {}",gradering)
        }
        else {
            log.trace( CONFIDENTIAL,"Fant ikke ident {} i ${cache.name} for sletting ved hendelse med gradering: {}", id.maskFnr(), gradering)
        }
    }
    private fun refresh(identer: List<String>, gradering: String) {
        identer.forEach { id ->
            pdl.medFamilie(id)
            pdl.medUtvidetFamile(id)
            log.info("Oppdaterte PDL cache for identer etter hendelse av type $gradering")
        }
    }
}

