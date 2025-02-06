package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.stereotype.Service

@Service
class DefaultTilgangTjeneste(private val kandidatTjeneste: KandidatTjeneste, private val saksbehandlerTjeneste: SaksbehandlerTjeneste, private val skjerming: SkjermingTjeneste) : TilgangsTjeneste{
    override fun sjekkTilgang(saksbehandlerId: NavId, kandidatId: Fødselsnummer) {

        val kandidat = kandidatTjeneste.kandidat(kandidatId)
        val saksbehandler = saksbehandlerTjeneste.saksbehandler(saksbehandlerId)

        if (kandidat.kreverGruppe(STRENGT_FORTROLIG) && !saksbehandler.kanBehandle(STRENGT_FORTROLIG)) {
            throw TilgangException("Saksbehandler har ikke tilgang til ${STRENGT_FORTROLIG.gruppeNavn}",
                kandidat,
                saksbehandler,
                "42",
                false)
        }


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
}

class TilgangException(melding: String, kandidat:  Kandidat, saksbehandler: Saksbehandler, kode: String, overstyrbar: Boolean) : IrrecoverableException(FORBIDDEN,
    "Tilgang nektet: $melding",mapOf(
        "kandidat" to kandidat.ident.verdi,
        "saksbehandler" to saksbehandler.attributter.navId.verdi,
        "kode" to kode,
        "overstyrbar" to overstyrbar))