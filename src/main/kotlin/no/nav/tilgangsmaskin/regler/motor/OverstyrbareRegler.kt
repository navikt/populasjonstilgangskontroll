package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.intervallSiden
import no.nav.tilgangsmaskin.regler.motor.GruppeMetadata.AVDØD
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component


interface OverstyrbarRegel : Regel

@Component
@Order(LOWEST_PRECEDENCE)
class NorgeRegel : GlobalGruppeRegel(NASJONAL), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { !(ansatt erMedlemAv NASJONAL) && !(ansatt kanBehandle bruker.geografiskTilknytning) }
}

@Component
@Order(LOWEST_PRECEDENCE - 1)
class UkjentBostedRegel : GlobalGruppeRegel(UKJENT_BOSTED), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { bruker.harUkjentBosted && !(ansatt erMedlemAv UKJENT_BOSTED) }
}

@Component
@Order(LOWEST_PRECEDENCE - 2)
class UtlandRegel : GlobalGruppeRegel(UTENLANDSK), OverstyrbarRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { bruker.harUtenlandskBosted && !(ansatt erMedlemAv UTENLANDSK) }
}

@Component
@Order(LOWEST_PRECEDENCE - 3)
class AvdødBrukerRegel(val teller: AvdødTeller) : TellendeRegel {
    override val predikat: (Ansatt, Bruker) -> Boolean = { _, bruker -> bruker.dødsdato != null }
    override val metadata = RegelMetadata(AVDØD)

    override fun tell(ansatt: Ansatt, bruker: Bruker) = teller.tell(Tags.of("months", bruker.dødsdato!!.intervallSiden()))
}







