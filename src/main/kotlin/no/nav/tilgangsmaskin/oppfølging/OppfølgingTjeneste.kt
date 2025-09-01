package no.nav.tilgangsmaskin.oppfølging

import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.Enhetsnummer
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class OppfølgingTjeneste {
    fun enhet(brukerId: BrukerId): Enhetsnummer? {
        // TODO implementer kall til oppfølging
        return null
    }
}