package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AnsattTjeneste(private val entra: EntraTjeneste) {  // kan slå opp mer her senere

    private val log = LoggerFactory.getLogger(AnsattTjeneste::class.java)

    fun ansatt(navId: NavId) = entra.ansatt(navId).also {
        log.trace(CONFIDENTIAL, "Ansatt: {}", it)
    }
}