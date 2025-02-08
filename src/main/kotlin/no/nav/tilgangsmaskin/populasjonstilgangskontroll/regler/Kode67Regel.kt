package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.FORTROLIG
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.FortroligGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Kandidat
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.Regel.RegelForklaring
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(HIGHEST_PRECEDENCE)

class Kode67Regel : Regel {
    private val log = getLogger(Kode67Regel::class.java)

    override fun test(k: Kandidat, s: Saksbehandler): Boolean {
        return when {
            s.kanBehandle(STRENGT_FORTROLIG) -> {
                log.trace("Saksbehandler kan behandle ${STRENGT_FORTROLIG.gruppeNavn}")
                k.kreverGruppe(STRENGT_FORTROLIG) || k.erUbeskyttet
            }
            s.kanBehandle(FORTROLIG) -> {
                log.trace("Saksbehandler kan behandle ${FORTROLIG.gruppeNavn}")
                k.kreverGruppe(FORTROLIG) || k.erUbeskyttet
            }
            else -> {
                log.trace("Saksbehandler kan behandle ubeskyttet")
                k.erUbeskyttet
            }
        }
    }

    override val forklaring: RegelForklaring
        get() = RegelForklaring("Beskyttelsesregler","Saksbehandler har ikke tilgang", "67")
}