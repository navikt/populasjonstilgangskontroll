package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.springframework.stereotype.Service

@Service
@Timed
class BrukerTjeneste(private val pdlTjeneste: PDLTjeneste,val egenAnsatt: SkjermingTjeneste) {


    fun brukerBulk(brukerIds: List<BrukerId>) = pdlTjeneste.personPipBulk(brukerIds)

    fun bruker(brukerId: BrukerId)  =
        run {
            val skjermet = egenAnsatt.erSkjermet(brukerId)
            val pip = pdlTjeneste.person(brukerId)
            tilBruker(brukerId, pip, skjermet)
        }
}
