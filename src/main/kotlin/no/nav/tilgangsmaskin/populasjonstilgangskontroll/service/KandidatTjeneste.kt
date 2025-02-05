package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PersonTjeneste
import org.springframework.stereotype.Service

@Service
class KandidatTjeneste(private val pdl: PersonTjeneste) {
    fun kandidat(fnr: Fødselsnummer) = pdl.kandidat(fnr) // kan slå opp mer her senere
}
@Service
class SaksbehandlerTjeneste(private val entra: EntraTjeneste) {  // kan slå opp mer her senere
    fun saksbehandler(navId: NavId) = entra.saksbehandler(navId)
}