package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.stereotype.Service

@Service
class DefaultTilgangsTjeneste(private val kandidatTjeneste: KandidatTjeneste, private val saksbehandlerTjeneste: SaksbehandlerTjeneste, private val skjerming: SkjermingTjeneste) : TilgangsTjeneste{
    override fun harTilgang(saksbehandlerId: NavId, kandidatId: Fødselsnummer): TilgangsRespons {

        val kandidat = kandidatTjeneste.kandidat(kandidatId)
        val saksbehandler = saksbehandlerTjeneste.saksbehandler(saksbehandlerId)

        if (kandidat.kreverGruppe(STRENGT_FORTROLIG) && !saksbehandler.kanBehandle(STRENGT_FORTROLIG))  {
             throw IrrecoverableException(FORBIDDEN, "Tilgang nektet, saksbehandler har ikke tilgang til ${STRENGT_FORTROLIG.gruppeNavn}", mapOf("kandidat" to kandidatId.verdi, "saksbehandler" to saksbehandler.attributter.id))
        }

        return tillat(saksbehandlerId,kandidatId)

      /*  if (fortrolig && !FORTROLIG_ADRESSE) avslå
        if (fortrolig utland %% !GA - STRENGT_FORTROLIG_ADRESSE) avslå
        if (skjerming && !GA-EGNE_ANSATTE) avslå
    }*/
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
    fun tillat(saksbehandler: NavId, kandidat: Fødselsnummer) =
            TilgangsRespons(kandidat, saksbehandler, true)
}

