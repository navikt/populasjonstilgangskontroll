package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SaksbehandlerTjeneste(private val entra: EntraTjeneste) {  // kan slå opp mer her senere

    private val log = LoggerFactory.getLogger(SaksbehandlerTjeneste::class.java)

    fun saksbehandler(navId: NavId) = entra.saksbehandler(navId).also {
        log.trace(CONFIDENTIAL, "Saksbehandler: {}", it)
    }
}