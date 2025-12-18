package no.nav.tilgangsmaskin.bruker.pdl


import no.nav.person.pdl.leesah.Personhendelse
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.`PdlCacheTømmerTeller`
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PdlCacheTømmer(private val teller: PdlCacheTømmerTeller) {
    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = [ "pdl.leesah-v1"],
        containerFactory = "pdlAvroListenerContainerFactory",
        filter = "graderingFilterStrategy")
    fun listen(hendelse: Personhendelse) {
        log.info("Mottok hendelse av tyoe ${hendelse.adressebeskyttelse?.gradering} fra PDL, tømmer cacher" )
        hendelse.personidenter.forEach(::evict)
        /*
        PDL_CACHES.forEach { cache ->
            hendelse.personidenter.forEach { id ->
                if (client.delete(cache, id = id) > 0) {
                    teller.tell(Tags.of("cache", cache.name, "gradering",
                        hendelse.adressebeskyttelse?.gradering?.name?.lowercase(getDefault()) ?: UGRADERT.name.lowercase(getDefault()),"type",hendelse.endringstype?.name ?: "N/A"))
                    log.trace(CONFIDENTIAL,"Slettet nøkkel ${client.tilNøkkel(cache, id)} fra cache ${cache.name} etter hendelse av type: {}", id.maskFnr(), Personhendelse::class.simpleName)
                    log.info("Slettet innslag fra cache ${cache.name} etter hendelse av type: {}", hendelse.adressebeskyttelse?.gradering)
                }
                else {
                    log.trace( CONFIDENTIAL,"Fant ikke ident {} i ${cache.name} for sletting ved hendelse av type: {}", id.maskFnr(), Personhendelse::class.simpleName)
                }
            }
        }*/
    }
    @Caching(
        evict = [
            CacheEvict(
                cacheNames = [PDL],
                key = "T(no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion).MED_UTVIDET_FAMILIE + ':' + #id"
            ),
            CacheEvict(
                cacheNames = [PDL],
                key = "T(no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion).MED_FAMILIE + ':' + #id"
            )
        ]
    )
    public fun evict(id: String) {
        log.info("Tømmer PDL caches for id: {}", id.maskFnr())
    }
}

