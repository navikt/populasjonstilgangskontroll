package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Tags
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.boot.conditionals.ConditionalOnProd
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.NASJONAL
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UKJENT_BOSTED
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.UTENLANDSK
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.oppfølging.OppfølgingTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.Dødsperiode
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.AVDØD
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
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

@Component
@Order(LOWEST_PRECEDENCE - 3)
@ConditionalOnProd
class AvdødBrukerTellendeRegel(private val teller: AvdødTeller, private val proxy: EntraProxyTjeneste, private val auditor: Auditor, private val token: Token) : TellendeRegel {

    override val metadata = RegelMetadata(AVDØD)

    override val skalTelle = { _: Ansatt, bruker: Bruker -> bruker.dødsdato != null }

    override fun tell(ansatt: Ansatt, bruker: Bruker) {
        val intervall = bruker.dødsdato!!.intervallSiden()
        with(enhet(intervall, ansatt)) {
            teller.tell(intervall, this)
            if (this != UTILGJENGELIG) {
                auditor.info("Ansatt ${ansatt.ansattId.verdi} i enhet $this fikk tilgang til forlengst avdød bruker ${bruker.brukerId.verdi} fra applikasjon ${token.system}")
            }
        }
    }

    private fun enhet(intervall: Dødsperiode, ansatt: Ansatt) =
        runCatching {
            intervall.takeIf { it >= Dødsperiode.MND_13_24 }
                ?.let { proxy.enhet(ansatt.ansattId).navn }
                ?: UTILGJENGELIG
        }.getOrDefault(UTILGJENGELIG)
}


@ConditionalOnNotProd
@Order(LOWEST_PRECEDENCE - 3)
@Component
class AvdødBrukerRegel : OverstyrbarRegel {

    override val metadata = RegelMetadata(AVDØD)

    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis {
            val dødsdato = bruker.dødsdato
            dødsdato != null && dødsdato.intervallSiden() > Dødsperiode.MND_7_12 && ansatt ikkeErMedlemAv GlobalGruppe.AVDØD
        }
}