package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import org.springframework.http.HttpStatus.FORBIDDEN

class RegelException(val brukerId: Fødselsnummer, val ansattId: NavId, val regel: Regel, detail: String = regel.beskrivelse.begrunnelseAnsatt.format(ansattId.verdi, brukerId.verdi, regel.beskrivelse.begrunnelse.årsak)) : IrrecoverableException(
    FORBIDDEN, detail,mapOf(
        "brukerIdent" to brukerId.verdi,
        "navIdent" to ansattId.verdi,
        //"begrunnelseKode" to regel.beskrivelse.begrunnelse,
       // "begrunnelseAnsatt" to detail,
        "kanOverstyres" to regel.erOverstyrbar))