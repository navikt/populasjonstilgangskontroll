package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.service.FortroligeGrupper

class Kandidat(val ident: Fødselsnummer, beskyttelse: FortroligeGrupper?) {
    val erStrengtFortrolig = FortroligeGrupper.STRENGT_FORTROLIG == beskyttelse
    val erFortrolig = FortroligeGrupper.FORTROLIG == beskyttelse

}