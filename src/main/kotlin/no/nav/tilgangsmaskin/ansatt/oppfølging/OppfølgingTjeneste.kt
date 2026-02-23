package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.Kontor
import no.nav.tilgangsmaskin.bruker.Identer
import no.nav.tilgangsmaskin.bruker.Identifikator
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
@Transactional
class OppfølgingTjeneste(private val db: OppfølgingJPAAdapter){


    @Cacheable(cacheNames = [OPPFØLGING],key = "#id.verdi")
    @Transactional(readOnly = true)
    fun enhetFor(id: Identifikator) =
        db.enhetFor(id.verdi)

    @Caching(
        put = [
            CachePut(cacheNames = [OPPFØLGING], key = "#identer.aktorId.verdi"),
            CachePut(cacheNames = [OPPFØLGING], key = "#identer.brukerId.verdi")
        ]
    )
    fun opprett(id: UUID, identer: Identer, kontor: Kontor, tidspunkt: Instant) =
        db.insert(id, identer.brukerId.verdi, identer.aktorId.verdi, tidspunkt, kontor.kontorId.verdi).also {
            log(id, "Oppfølging startet for", kontor)
        }

    fun oppdater(id: UUID, kontor: Kontor, tidspunkt: Instant) =
        db.update(id, tidspunkt, kontor.kontorId.verdi)
            ?.also { log(id, "Oppfølging kontor endret til", kontor) }

    @Caching(
        evict = [
            CacheEvict(cacheNames = [OPPFØLGING], key = "#identer.aktorId.verdi"),
            CacheEvict(cacheNames = [OPPFØLGING], key = "#identer.brukerId.verdi")
        ]
    )
    fun avslutt(id: UUID, identer: Identer) =
        db.delete(id).also {
            log(id,"Oppfølging avsluttet for")
        }

    companion object {
        private val log = getLogger(OppfølgingTjeneste::class.java)
        private fun log(id: UUID, melding: String, kontor: Kontor? = null) =
            log.info("$melding ${kontor?.let { "${it.kontorId} " } ?: ""}for $id")
    }
}