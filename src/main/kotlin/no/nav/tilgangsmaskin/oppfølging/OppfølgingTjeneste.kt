package no.nav.tilgangsmaskin.oppfølging

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class OppfølgingTjeneste : CachableRestConfig {
    @Cacheable(cacheNames = [OPPFØLGING],key = "#brukerId.verdi")
    fun enhetFor(brukerId: BrukerId): Enhetsnummer? {
        // TODO implementer kall til oppfølging
        return null
    }

    override val varighet = Duration.ofHours(12)
    override val navn = OPPFØLGING

    companion object {
        const val OPPFØLGING = "oppfølging"
    }
}




