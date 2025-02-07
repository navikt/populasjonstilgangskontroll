package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.FORTROLIG
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import org.springframework.stereotype.Component

typealias RegelPredikat = (Kandidat, Saksbehandler) -> Boolean


@Component
class StatiskRegelMotor : RegelMotor{

    data class Regel(val regel: RegelPredikat, val navn: String, val feilmelding: String, val kode: String, val overstyrbar: Boolean = false)

    override fun vurderTilgang(k: Kandidat, s: Saksbehandler) {
           regler.forEach {
               with(it) {
                   print ("Evaluating $navn")
                   val status = regel.invoke(k, s)
                   println(" -> $status")
                   if (!status)
                       throw TilgangException(feilmelding, k, s, kode, overstyrbar)
               }
           }
    }

    companion object {
        private val kode67 : RegelPredikat = { k, s ->
            if (s.kanBehandle(STRENGT_FORTROLIG)) {
                k.kreverGruppe(STRENGT_FORTROLIG) || k.beskyttelse == null
            }
            else if (s.kanBehandle(FORTROLIG)) {
                k.kreverGruppe(FORTROLIG) || k.beskyttelse == null
            } else {
                k.beskyttelse == null
            }
        }
        private val regler = listOf(
            Regel(kode67, "kode 67","Saksbehandler har ikke tilgang til ${STRENGT_FORTROLIG.gruppeNavn}", "67"))
    }
}

