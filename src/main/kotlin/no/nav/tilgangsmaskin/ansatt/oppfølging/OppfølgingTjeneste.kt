package no.nav.tilgangsmaskin.ansatt.oppfølging

import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingHendelse.Kontor
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class OppfølgingTjeneste(private val db: OppfølgingJPAAdapter) {

    @Cacheable(cacheNames = [OPPFØLGING],key = "#id")
    fun enhetFor(id: String) =
        db.enhetFor(id)

    @Caching(
        evict = [
            CacheEvict(cacheNames = [OPPFØLGING], key = "#aktorId.verdi"),
            CacheEvict(cacheNames = [OPPFØLGING], key = "#brukerId.verdi")
        ]
    )
    fun start(oppfolgingsperiodeUuid: UUID, brukerId: BrukerId, aktorId: AktørId, tidspunkt: Instant, kontor: Kontor) =
        db.startOppfølging(oppfolgingsperiodeUuid,brukerId.verdi, aktorId.verdi, tidspunkt,kontor.kontorId.verdi)

    @Caching(
        evict = [
            CacheEvict(cacheNames = [OPPFØLGING], key = "#aktorId.verdi"),
            CacheEvict(cacheNames = [OPPFØLGING], key = "#brukerId.verdi")
        ]
    )
    fun kontorFor(oppfolgingsperiodeUuid: UUID, brukerId: BrukerId, aktorId: AktørId, tidspunkt: Instant, kontor: Kontor) =
        db.oppdaterKontor(oppfolgingsperiodeUuid,brukerId.verdi, aktorId.verdi, tidspunkt,kontor.kontorId.verdi)

    @Caching(
        evict = [
            CacheEvict(cacheNames = [OPPFØLGING], key = "#aktorId.verdi"),
            CacheEvict(cacheNames = [OPPFØLGING], key = "#brukerId.verdi")
        ]
    )
    fun avslutt(oppfolgingsperiodeUuid: UUID, brukerId: BrukerId, aktorId: AktørId) =
        db.avsluttOppfølging(oppfolgingsperiodeUuid)
}