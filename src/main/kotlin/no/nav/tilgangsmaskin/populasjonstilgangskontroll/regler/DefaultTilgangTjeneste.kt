package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.springframework.stereotype.Service

@Service
class DefaultTilgangTjeneste(private val regelMotor: RegelMotor,private val kandidatTjeneste: KandidatTjeneste, private val saksbehandlerTjeneste: SaksbehandlerTjeneste, private val skjerming: SkjermingTjeneste) : TilgangTjeneste{
    override fun sjekkTilgang(saksbehandlerId: NavId, kandidatId: Fødselsnummer) =

        regelMotor.vurderTilgang(kandidatTjeneste.kandidat(kandidatId), saksbehandlerTjeneste.saksbehandler(saksbehandlerId))
        /**
        Prioritert utslagskriterier:
        Harde regler:
        Kode 6: Strengt fortrolig adresse
        Kode 19: trengt fortrolig adresse utland
        Kode 7 : Fortrolig adresse
        Egen ansatt: Skjerming
        Familie: (mangler datasettene for dette)
        Verge: (ikkje implementert og mangler datasettene for dette)
        Oppslag på egen person :(mangler datasettene for dette) (hovuddel vil håndters via skjerming, men avskjermede ansatte dekkes ikkje av skjerming)
        Overstyrbare regler:
        Geogrfisk tilgang: (Mangler datasettene for dette)
         **/

}

