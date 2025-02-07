package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.FORTROLIG
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import org.springframework.stereotype.Component

typealias RegelPredikat = (Kandidat, Saksbehandler) -> Boolean


@Component
class StatiskRegelMotor : RegelMotor{

    data class Regel(val regel: RegelPredikat, val beskrivelse: String, val kode: String,val overstyrbar: Boolean = false)

    override fun vurderTilgang(k: Kandidat, s: Saksbehandler) {
           regler.forEach {
               with(it) {
                   if (!regel.invoke(k, s))
                       throw TilgangException(beskrivelse, k, s, kode, overstyrbar)
               }
           }
    }

    companion object {
        private val kode6 : RegelPredikat = { k, s -> k.kreverGruppe(STRENGT_FORTROLIG) && s.kanBehandle(STRENGT_FORTROLIG) }
        private val kode7 : RegelPredikat = { k, s -> k.kreverGruppe(FORTROLIG) && s.kanBehandle(FORTROLIG) }

        private val regler = listOf(
            Regel(kode6, "Saksbehandler har ikke tilgang til ${STRENGT_FORTROLIG.gruppeNavn}", "6"),
            Regel(kode7, "Saksbehandler har ikke tilgang til ${FORTROLIG.gruppeNavn}","7"))
    }
}

