package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.Kontor
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Identifikator
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.Instant.now
import java.util.UUID

@Service
@Transactional
class OppfølgingTjeneste(private val db: OppfølgingJPAAdapter) {

    @Cacheable(cacheNames = [OPPFØLGING],key = "#id.verdi")
    @Transactional(readOnly = true)
    fun enhetFor(id: Identifikator) =
        db.enhetFor(id.verdi)

    @Caching(
        put = [
            CachePut(cacheNames = [OPPFØLGING], key = "'${OPPFØLGING}::' + #aktorId.verdi"),
            CachePut(cacheNames = [OPPFØLGING], key = "'${OPPFØLGING}::' + #brukerId.verdi")
        ]
    )
    fun start(oppfolgingsperiodeUuid: UUID, brukerId: BrukerId, aktorId: AktørId, kontor: Kontor, tidspunkt: Instant = now()) =
        db.startOppfølging(oppfolgingsperiodeUuid, brukerId.verdi, aktorId.verdi, kontor.kontorId.verdi, tidspunkt)

    @Caching(
        put = [
            CachePut(cacheNames = [OPPFØLGING], key = "'${OPPFØLGING}::' + #aktorId.verdi"),
            CachePut(cacheNames = [OPPFØLGING], key = "'${OPPFØLGING}::' + #brukerId.verdi")
        ]
    )
    fun kontorFor(oppfolgingsperiodeUuid: UUID, brukerId: BrukerId, aktorId: AktørId, kontor: Kontor, tidspunkt: Instant = now()) =
        db.oppdaterKontor(oppfolgingsperiodeUuid, brukerId.verdi, aktorId.verdi, kontor.kontorId.verdi, tidspunkt)

    @Caching(
        evict = [
            CacheEvict(cacheNames = [OPPFØLGING], key = "'${OPPFØLGING}::' + #aktorId.verdi"),
            CacheEvict(cacheNames = [OPPFØLGING], key = "'${OPPFØLGING}::' + #brukerId.verdi")
        ]
    )
    fun avslutt(oppfolgingsperiodeUuid: UUID, brukerId: BrukerId, aktorId: AktørId) =
        db.avsluttOppfølging(oppfolgingsperiodeUuid)
}