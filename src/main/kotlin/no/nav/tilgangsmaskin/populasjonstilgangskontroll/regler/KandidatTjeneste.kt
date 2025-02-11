package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLGraphQLClientAdapter
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingRestClientAdapter
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@Service
class KandidatTjeneste(private val pdl: PDLGraphQLClientAdapter, val egenAnsatt: SkjermingRestClientAdapter) {

    private val log = getLogger(KandidatTjeneste::class.java)

    fun kandidat(fnr: Fødselsnummer) : Kandidat {
        val person =  pdl.person(fnr.verdi)
        val skjermet = egenAnsatt.skjermetPerson(fnr.verdi)
        return KandidatMapper.mapToKandidat(fnr,person, skjermet).also {
            log.info(CONFIDENTIAL,"Kandidat: $it")
        }
    }// kan slå opp mer her senere

}
@Service
class SaksbehandlerTjeneste(private val entra: EntraTjeneste) {  // kan slå opp mer her senere

    private val log = getLogger(SaksbehandlerTjeneste::class.java)

    fun saksbehandler(navId: NavId) = entra.saksbehandler(navId).also {
        log.info(CONFIDENTIAL,"Saksbehandler: $it")
    }
}