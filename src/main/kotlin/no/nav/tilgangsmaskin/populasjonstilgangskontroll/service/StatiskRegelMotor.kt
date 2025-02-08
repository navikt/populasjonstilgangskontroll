package no.nav.tilgangsmaskin.populasjonstilgangskontroll.service

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import org.springframework.stereotype.Component
import java.util.function.BiPredicate

@Component
class StatiskRegelMotor : RegelMotor {

    override fun vurderTilgang(k: Kandidat, s: Saksbehandler) =
        regler.forEach { regel ->
            if (!regel.predikat.test(k, s)) {
                throw TilgangException(regel.feilmelding, k, s, regel.kode, regel.overstyrbar)
            }
        }
    private val regler = listOf(
        Regel(Kode67Regel, "Beskyttelsesregler","Saksbehandler har ikke tilgang til ${STRENGT_FORTROLIG.gruppeNavn}", "67"))
    }

private data class Regel(val predikat: BiPredicate<Kandidat, Saksbehandler>, val navn: String, val feilmelding: String, val kode: String, val overstyrbar: Boolean = false)



