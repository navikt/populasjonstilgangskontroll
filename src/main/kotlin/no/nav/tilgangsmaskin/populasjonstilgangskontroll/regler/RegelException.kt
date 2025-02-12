package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import org.springframework.http.HttpStatus.FORBIDDEN
import java.lang.String.format

class RegelException(brukerId: Fødselsnummer, ansattId: NavId, regel: Regel) : IrrecoverableException(
    FORBIDDEN, format(regel.beskrivelse.feilmelding,ansattId.verdi, brukerId.verdi),mapOf(
        "bruker" to brukerId.verdi,
        "ansatt" to ansattId.verdi,
        "kode" to regel.beskrivelse.kode,
        "navn" to regel.beskrivelse.navn,
        "overstyrbar" to regel.beskrivelse.overstyrbar))