package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import org.springframework.http.HttpStatus.FORBIDDEN
import java.lang.String.format

class RegelException(brukerId: Fødselsnummer, ansattId: NavId, regel: Regel) : IrrecoverableException(
    FORBIDDEN, format(regel.beskrivelse.feilmelding,ansattId.verdi, brukerId.verdi),mapOf(
        "brukerIdent" to brukerId.verdi,
        "navIdent" to ansattId.verdi,
        "begrunnelsesKode" to regel.beskrivelse.kode,
        "navn" to regel.beskrivelse.navn,
        "kanOverstyres" to regel.beskrivelse.overstyrbar))