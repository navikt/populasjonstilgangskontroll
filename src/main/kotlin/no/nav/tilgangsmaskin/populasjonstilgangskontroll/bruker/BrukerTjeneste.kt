package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipRespons
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PdlPipTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.springframework.stereotype.Service

@Service
@Timed
class BrukerTjeneste(private val pdlTjeneste: PDLTjeneste,val egenAnsatt: SkjermingTjeneste) {

    fun brukere(brukerIds: List<BrukerId>) =
        run {
            val skjerminger = egenAnsatt.skjerminger(brukerIds)
            val personer  = pdlTjeneste.personer(brukerIds)
            personer.map { (key : BrukerId, value: PdlPipRespons) ->
                tilBruker(key, value, skjerminger[key] ?: false)
            }
        }

    fun bruker(brukerId: BrukerId)  =
        run {
            val skjermet = egenAnsatt.skjerming(brukerId)
            val person = pdlTjeneste.person(brukerId)
            tilBruker(brukerId, person, skjermet)
        }
}
