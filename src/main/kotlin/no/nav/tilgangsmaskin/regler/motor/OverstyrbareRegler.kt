package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.oppfølging.OppfølgingTjeneste
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component


interface OverstyrbarRegel : Regel

@Component
@Order(LOWEST_PRECEDENCE)
class NorgeRegel(private val oppfølging: OppfølgingTjeneste) : GlobalGruppeRegel(NASJONAL), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { ansatt ikkeErMedlemAv NASJONAL && ansatt kanIkkeBehandle bruker.geografiskTilknytning  }

    override val postCondition: (ansatt: Ansatt, bruker: Bruker) -> Boolean = { ansatt, bruker ->
        val enhet = oppfølging.enhet(bruker.brukerId)
        val copy = bruker.copy(oppfølgingsenhet = enhet)
        godtaHvis { copy.oppfølgingsenhet != null } // TODO sjekk gruppe
    }
}

@Component
@Order(LOWEST_PRECEDENCE - 1)
class UkjentBostedRegel : GlobalGruppeRegel(UKJENT_BOSTED), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { bruker.harUkjentBosted && ansatt ikkeErMedlemAv UKJENT_BOSTED }
}

@Component
@Order(LOWEST_PRECEDENCE - 2)
class UtlandRegel : GlobalGruppeRegel(UTENLANDSK), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { bruker.harUtenlandskBosted && ansatt ikkeErMedlemAv UTENLANDSK }
}









