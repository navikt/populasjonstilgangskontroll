package no.nav.tilgangsmaskin.ansatt.oppfølging

import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingConfig.Companion.OPPFØLGING
import no.nav.tilgangsmaskin.ansatt.oppfølging.Oppfølgingsendring.Avsluttet
import no.nav.tilgangsmaskin.ansatt.oppfølging.Oppfølgingsendring.MedKontor
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.regler.motor.OppfølgingkontorTeller
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OppfølgingTjeneste(private val adapter: OppfølgingJPAAdapter, private val teller: OppfølgingkontorTeller) {

    @Cacheable(cacheNames = [OPPFØLGING], key = "#id.verdi")
    @Transactional(readOnly = true)
    fun enhetFor(id: Identifikator) =
        adapter.enhetFor(id.verdi).also { enhet ->
            teller.tell(Tags.of("resultat", "${enhet != null}"))
        }

    @Caching(
        put = [
            CachePut(cacheNames = [OPPFØLGING], key = "#endring.identer.aktorId.verdi"),
            CachePut(cacheNames = [OPPFØLGING], key = "#endring.identer.brukerId.verdi")
        ]
    )
    fun registrer(endring: MedKontor) =
        with(endring) {
            kontor.kontorId.also {
                adapter.registrer(uuid, identer.brukerId.verdi,
                    identer.aktorId.verdi, tidspunkt, it.verdi, )
            }
        }


    @Caching(
        evict = [
            CacheEvict(cacheNames = [OPPFØLGING], key = "#endring.identer.aktorId.verdi"),
            CacheEvict(cacheNames = [OPPFØLGING], key = "#endring.identer.brukerId.verdi")
        ]
    )
    fun avslutt(endring: Avsluttet) =
        adapter.avslutt(endring.uuid)
}