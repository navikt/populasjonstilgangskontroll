package no.nav.tilgangsmaskin.bruker.pdl


import io.micrometer.core.instrument.Tags
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_CACHES
import no.nav.tilgangsmaskin.bruker.pdl.Person.Gradering.UGRADERT
import no.nav.tilgangsmaskin.felles.cache.LettuceCacheClient
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.`PdlCacheTømmerTeller`
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.Locale.getDefault

@Component
class PdlCacheTømmer(private val client: LettuceCacheClient, private val teller: PdlCacheTømmerTeller) {
    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = [ "pdl.leesah-v1"],
        containerFactory = "pdlAvroListenerContainerFactory",
        filter = "graderingFilterStrategy")
    fun listen(hendelse: Personhendelse) {
        log.info("Mottok hendelse av tyoe ${hendelse.adressebeskyttelse?.gradering} fra PDL, tømmer cacher" )
        PDL_CACHES.forEach { cache ->
            hendelse.personidenter.forEach { id ->
                if (client.delete(id,cache) > 0) {
                    teller.tell(Tags.of("cache", cache.name, "gradering",
                        hendelse.adressebeskyttelse?.gradering?.name?.lowercase(getDefault()) ?: UGRADERT.name.lowercase(getDefault()),"type",hendelse.endringstype?.name ?: "N/A"))
                    log.trace(CONFIDENTIAL,"Slettet nøkkel ${client.tilNøkkel(cache, id)} fra cache ${cache.name} etter hendelse av type: {}", id.maskFnr(), Personhendelse::class.simpleName)
                    log.info("Slettet innslag fra cache ${cache.name} etter hendelse av type: {}", hendelse.adressebeskyttelse?.gradering)
                }
                else {
                    log.trace( CONFIDENTIAL,"Fant ikke ident {} i ${cache.name} for sletting ved hendelse av type: {}", id.maskFnr(), Personhendelse::class.simpleName)
                }
            }
        }
    }
}

