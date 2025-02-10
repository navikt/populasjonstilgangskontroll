package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelForklaring
import org.springframework.http.HttpStatus.FORBIDDEN
import java.lang.String.format

class RegelException(kandidatId: Fødselsnummer, saksbehandlerId: NavId, forklaring: RegelForklaring) : IrrecoverableException(
    FORBIDDEN, format(forklaring.feilmelding,saksbehandlerId.verdi, kandidatId.verdi),mapOf(
        "kandidat" to kandidatId.verdi,
        "saksbehandler" to saksbehandlerId.verdi,
        "kode" to forklaring.kode,
        "overstyrbar" to forklaring.overstyrbar))