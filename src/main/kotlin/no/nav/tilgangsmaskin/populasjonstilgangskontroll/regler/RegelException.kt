package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import org.springframework.http.HttpStatus.FORBIDDEN
import java.lang.String.format

class RegelException(brukerId: Fødselsnummer, ansattId: NavId, regel: Regel) : IrrecoverableException(
    FORBIDDEN,null,mapOf(
        "brukerIdent" to brukerId.verdi,
        "navIdent" to ansattId.verdi,
        "begrunnelseKode" to regel.beskrivelse.kode,
        "begrunnelseAnsatt" to regel.beskrivelse.begrunnelseAnsatt.format(ansattId.verdi, brukerId.verdi, regel.beskrivelse.kode.årsak),
        "kanOverstyres" to regel.beskrivelse.overstyrbar))