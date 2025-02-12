package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import org.springframework.http.HttpStatus.FORBIDDEN

class RegelException(brukerId: Fødselsnummer, ansattId: NavId, val regel: Regel) : IrrecoverableException(
    FORBIDDEN,null,mapOf(
        "brukerIdent" to brukerId.verdi,
        "navIdent" to ansattId.verdi,
        "begrunnelseKode" to regel.beskrivelse.begrunnelse,
        "begrunnelseAnsatt" to regel.beskrivelse.begrunnelseAnsatt.format(ansattId.verdi, brukerId.verdi, regel.beskrivelse.begrunnelse.årsak),
        "kanOverstyres" to regel.beskrivelse.overstyrbar))