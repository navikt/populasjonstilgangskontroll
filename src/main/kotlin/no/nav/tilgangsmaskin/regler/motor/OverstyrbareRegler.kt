package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component


interface OverstyrbarRegel : Regel

@Component
@Order(LOWEST_PRECEDENCE)
class GeografiskRegel(private val oppfølging: OppfølgingTjeneste) : GlobalGruppeRegel(NASJONAL), OverstyrbarRegel {
    protected val log = getLogger(javaClass)
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        godtaHvis {
            ansatt erMedlemAv NASJONAL
                    || ansatt kanBehandle bruker.geografiskTilknytning
                    || (ansatt tilhører oppfølging.enhetFor(bruker.oppslagId)).also {
                        if (it) {
                            log.info("Ansatt ${ansatt.ansattId.verdi} kan behandle bruker ${bruker.oppslagId.maskFnr()} via oppfølgingsenhet")
                        }
                        else {
                            log.warn("Ansatt ${ansatt.ansattId.verdi} kan *IKKE* behandle bruker ${bruker.oppslagId.maskFnr()} via oppfølgingsenhet")
                    }
            }
        }
}

@Component
@Order(LOWEST_PRECEDENCE - 1)
class UkjentBostedRegel : GlobalGruppeRegel(UKJENT_BOSTED), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            bruker.harUkjentBosted && ansatt ikkeErMedlemAv UKJENT_BOSTED
        }
}

@Component
@Order(LOWEST_PRECEDENCE - 2)
class UtlandRegel : GlobalGruppeRegel(UTENLANDSK), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            bruker.harUtenlandskBosted && ansatt ikkeErMedlemAv UTENLANDSK
        }
}








