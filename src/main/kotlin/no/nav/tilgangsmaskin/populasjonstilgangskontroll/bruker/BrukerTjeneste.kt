package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.springframework.stereotype.Service

@Service
@Timed
class BrukerTjeneste(private val pdlTjeneste: PDLTjeneste,val egenAnsatt: SkjermingTjeneste) {

    fun brukere(brukerIds: List<BrukerId>) = pdlTjeneste.personer(brukerIds).map {
        tilBruker(it.key, it.value, false) // TODO skjerming
    }

    fun bruker(brukerId: BrukerId)  =
        run {
            val skjermet = egenAnsatt.erSkjermet(brukerId)
            val person = pdlTjeneste.person(brukerId)
            tilBruker(brukerId, person, skjermet)
        }
}
