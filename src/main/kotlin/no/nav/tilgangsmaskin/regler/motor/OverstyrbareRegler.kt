package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Tags
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.AVDØD
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.VERGEMÅL
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.stereotype.Component


interface OverstyrbarRegel : Regel

@SortertRegel(LOWEST_PRECEDENCE)
class GeografiskRegel(private val oppfølging: OppfølgingTjeneste,private val teller: OppfølgingkontorTeller) : GlobalGruppeRegel(NASJONAL), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        godtaHvis {
            ansatt erMedlemAv NASJONAL
                    || ansatt kanBehandle bruker.geografiskTilknytning
                    || (ansatt tilhører oppfølging.enhetFor(Identifikator(bruker.oppslagId))).also {
                teller.tell(Tags.of("resultat", "$it"))
            }
        }
}

@SortertRegel(LOWEST_PRECEDENCE - 1)
class UkjentBostedRegel : GlobalGruppeRegel(UKJENT_BOSTED), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            bruker.harUkjentBosted && ansatt ikkeErMedlemAv UKJENT_BOSTED
        }
}

@SortertRegel(LOWEST_PRECEDENCE - 2)
class UtlandRegel : GlobalGruppeRegel(UTENLANDSK), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            bruker.harUtenlandskBosted && ansatt ikkeErMedlemAv UTENLANDSK
        }
}

@SortertRegel(LOWEST_PRECEDENCE - 3)
@ConditionalOnNotProd
@Component
class AvdødBrukerRegel : OverstyrbarRegel {

    override val metadata = RegelMetadata(AVDØD)

    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            bruker.erForlengstAvdød && ansatt ikkeErMedlemAv GlobalGruppe.AVDØD
        }
}

@SortertRegel(LOWEST_PRECEDENCE - 4)
@ConditionalOnNotProd
class VergemålRegel(private val vergemål: VergemålTjeneste) : OverstyrbarRegel {

    private val log = getLogger(javaClass)

    override val metadata = RegelMetadata(VERGEMÅL)

    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            runCatching {
                vergemål.vergemål(ansatt.ansattId).contains(bruker.brukerId)
            }.getOrElse {
                log.error("Feil ved sjekk av vergemål for ansatt ${ansatt.ansattId.verdi}", it)
                false
            }
        }
}
