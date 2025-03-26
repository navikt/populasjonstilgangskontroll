package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.springframework.stereotype.Service

@Service
@Timed
class BrukerTjeneste(private val pdl: PDLTjeneste, val egenAnsatt: SkjermingTjeneste) {

    fun brukere(brukerIds: List<BrukerId>) =
        run {
            val skjerminger = egenAnsatt.skjerminger(brukerIds)
            pdl.personer(brukerIds).map {
                tilBruker(it, skjerminger[it.brukerId] ?: false)
            }
        }

    fun bruker(brukerId: BrukerId)  =
        run {
            val skjermet = egenAnsatt.skjerming(brukerId)
            val person = pdl.person(brukerId)
            tilBruker(person, skjermet)
        }
}
