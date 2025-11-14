package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component


interface OverstyrbarRegel : Regel

@OrderedComponent(LOWEST_PRECEDENCE)
class GeografiskRegel(private val oppfølging: OppfølgingTjeneste,private val teller: OppfølgingkontorTeller) : GlobalGruppeRegel(NASJONAL), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        godtaHvis {
            ansatt erMedlemAv NASJONAL
                    || ansatt kanBehandle bruker.geografiskTilknytning
                    || (ansatt tilhører oppfølging.enhetFor(bruker.oppslagId)).also {
                teller.tell(Tags.of("resultat", "$it"))
            }
        }
}

@OrderedComponent(LOWEST_PRECEDENCE - 1)
class UkjentBostedRegel : GlobalGruppeRegel(UKJENT_BOSTED), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            bruker.harUkjentBosted && ansatt ikkeErMedlemAv UKJENT_BOSTED
        }
}

@OrderedComponent(LOWEST_PRECEDENCE - 2)
class UtlandRegel : GlobalGruppeRegel(UTENLANDSK), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            bruker.harUtenlandskBosted && ansatt ikkeErMedlemAv UTENLANDSK
        }
}








