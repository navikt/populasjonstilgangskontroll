package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import io.micrometer.core.annotation.Timed
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.PersonTilBrukerMapper.tilBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.springframework.stereotype.Service

@Service
@Timed
class BrukerTjeneste(private val personer: PDLTjeneste, val skjerminger: SkjermingTjeneste) {

    fun brukere(brukerIds: List<BrukerId>) =
        run {
            val skjerminger = skjerminger.skjerminger(brukerIds)
            personer.personer(brukerIds).map {
                tilBruker(it, skjerminger[it.brukerId] ?: false)
            }
        }

    fun bruker(brukerId: BrukerId)  =
        run {
            val skjermet = skjerminger.skjerming(brukerId)
            val person = personer.person(brukerId.verdi)
            tilBruker(person, skjermet)
        }
}
