package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableException
import org.springframework.http.HttpStatus

class TilgangException(melding: String, kandidat: Kandidat, saksbehandler: Saksbehandler, kode: String, overstyrbar: Boolean) : IrrecoverableException(
    HttpStatus.FORBIDDEN,
    "Tilgang nektet: $melding",mapOf(
        "kandidat" to kandidat.ident.verdi,
        "saksbehandler" to saksbehandler.attributter.navId.verdi,
        "kode" to kode,
        "overstyrbar" to overstyrbar))