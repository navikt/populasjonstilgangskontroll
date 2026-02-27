package no.nav.tilgangsmaskin.bruker.pdl


import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering.UGRADERT
import no.nav.tilgangsmaskin.bruker.pdl.PdlClientBeanConfig.Companion.PDL_CONTAINER_FACTORY
import no.nav.tilgangsmaskin.bruker.pdl.PdlClientBeanConfig.Companion.PDL_GRADERING_FILTER
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL_CACHES
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.motor.PdlCacheTømmerTeller
import org.slf4j.LoggerFactory.getLogger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PdlCacheOpprydder(private val pdl: PdlTjeneste,
                        private val client: CacheClient,
                        private val teller: PdlCacheTømmerTeller) {
    private val log = getLogger(javaClass)

    @KafkaListener(
        topics = [PDL_LEESAH_TOPIC],
        containerFactory = PDL_CONTAINER_FACTORY,
        filter = PDL_GRADERING_FILTER)
    fun listen(hendelse: Personhendelse) {
        val gradering = (hendelse.adressebeskyttelse?.gradering ?: UGRADERT).name
        val endringsType = hendelse.endringstype?.name ?: UTILGJENGELIG
        PDL_CACHES.forEach { cache ->
            hendelse.personidenter.forEach { id ->
                slett(cache, id, gradering, endringsType)
            }
            refresh(hendelse.personidenter, gradering)
        }
    }

    private fun slett(cache: CachableConfig, id: String, gradering: String, endringsType: String) {
        if (client.delete(cache, id) > 0) {
            teller.tell(cache, gradering,endringsType)
            log.trace(CONFIDENTIAL,"Slettet nøkkel ${client.tilNøkkel(cache, id)} fra cache ${cache.name} etter hendelse av type: {}", id.maskFnr(), gradering)
            log.info("Slettet innslag fra cache ${cache.name} etter hendelse med gradering: {}",gradering)
        }
        else {
            log.trace( CONFIDENTIAL,"Fant ikke ident {} i ${cache.name} for sletting ved hendelse med gradering: {}", id.maskFnr(), gradering)
        }
    }
    private fun refresh(identer: List<String>, gradering: String) =
        identer.forEach { id ->
            pdl.medFamilie(id)
            pdl.medUtvidetFamile(id)
            log.info("Oppdaterte PDL caches for identer etter hendelse av type $gradering")
        }

    private companion object {
        private const val PDL_LEESAH_TOPIC = "pdl.leesah-v1"
    }
}

