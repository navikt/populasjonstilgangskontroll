package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import org.springframework.http.HttpStatus.FORBIDDEN
import java.lang.String.format

class RegelException(kandidatId: Fødselsnummer, saksbehandlerId: NavId, regel: Regel) : IrrecoverableException(
    FORBIDDEN, format(regel.beskrivelse.feilmelding,saksbehandlerId.verdi, kandidatId.verdi),mapOf(
        "kandidat" to kandidatId.verdi,
        "saksbehandler" to saksbehandlerId.verdi,
        "kode" to regel.beskrivelse.kode,
        "navn" to regel.beskrivelse.navn,
        "overstyrbar" to regel.beskrivelse.overstyrbar))