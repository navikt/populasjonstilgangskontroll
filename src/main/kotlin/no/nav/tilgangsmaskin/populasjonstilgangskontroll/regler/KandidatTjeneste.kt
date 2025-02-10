package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PersonTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
class KandidatTjeneste(private val pdl: PersonTjeneste, val egenAnsatt: SkjermingTjeneste) {

    private val log = getLogger(KandidatTjeneste::class.java)

    fun kandidat(fnr: Fødselsnummer) : Kandidat {
        return pdl.kandidat(fnr).copy(egenAnsatt = egenAnsatt(fnr)).also {
            log.info(CONFIDENTIAL,"Kandidat: $it") }
    }// kan slå opp mer her senere


    private fun egenAnsatt(fnr: Fødselsnummer) = egenAnsatt.erSkjermet(fnr).also {
        log.info(CONFIDENTIAL,"Egen ansatt skjerming status: $it")
    }
}
@Service
class SaksbehandlerTjeneste(private val entra: EntraTjeneste) {  // kan slå opp mer her senere

    private val log = getLogger(SaksbehandlerTjeneste::class.java)

    fun saksbehandler(navId: NavId) = entra.saksbehandler(navId).also {
        log.info(CONFIDENTIAL,"Saksbehandler: $it")
    }
}