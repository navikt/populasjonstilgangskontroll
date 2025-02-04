package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PersonTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangsRespons.Begrunnelse
import org.springframework.stereotype.Service

@Service
class DefaultTilgangsTjeneste(private val pdl: PersonTjeneste, private val entra: AnsattTjeneste, private val skjerming: SkjermingTjeneste) : TilgangsTjeneste{
    override fun harTilgang(saksbehandlerId: NavId, kandidatId: Fødselsnummer): TilgangsRespons {

        val kandidat = pdl.kandidat(kandidatId)
        val saksbehandler = entra.saksbehandler(saksbehandlerId)

        if (kandidat.erStrengtFortrolig && !saksbehandler.kanBehandleStrengtFortrolig)
            return respons(saksbehandler.ident,kandidat.ident,false)

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

        return respons(saksbehandler.ident,kandidat.ident,true)

    }
    fun respons(saksbehandler: NavId, kandidat: Fødselsnummer, tilgang: Boolean) =
        TilgangsRespons(kandidat, saksbehandler, tilgang, Begrunnelse("Begrunnelse", "42", false))
}

