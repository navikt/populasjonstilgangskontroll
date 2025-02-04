package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.AdressebeskyttelseGradering
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PersonTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.TilgangsRespons.Begrunnelse
import org.springframework.stereotype.Service

@Service
class DefaultTilgangsTjeneste(private val pdl: PersonTjeneste, private val entra: AnsattTjeneste, private val skjerming: SkjermingTjeneste) : TilgangsTjeneste{
    override fun harTilgang(saksbehandler: NavId, kandidat: Fødselsnummer): TilgangsRespons {

        val person = pdl.hentPerson(kandidat)
        val id  = entra.ansattAzureId(saksbehandler)
        val grupper = entra.ansattTilganger(id).map { it.displayName }

        val strengtFortrolig = person?.adressebeskyttelse?.any { it.gradering == AdressebeskyttelseGradering.STRENGT_FORTROLIG } ?: false
        if (strengtFortrolig && !grupper.contains("GA_STRENGT_FORTROLIG_ADRESSE")) return avslå(saksbehandler, kandidat)
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


        return TilgangsRespons(kandidat, saksbehandler,true, null)
    }
    fun avslå(saksbehandler: NavId, kandidat: Fødselsnummer) =
         TilgangsRespons(
            begrunnelse = Begrunnelse(
                begrunnelse = "Begrunnelse",
                kode = "42",
                overstyrbar = false),
            kandidat = kandidat,
            saksbehandler = saksbehandler,
            tilgang = false
        )
}

