package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.EGEN_ANSATT
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.FORTROLIG
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe.STRENGT_FORTROLIG
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.EGNEDATA
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.FORELDREBARN
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.FORTROLIG_ADRESSE
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.PARTNER
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.SKJERMING
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.STRENGT_FORTROLIG_ADRESSE
import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster.SØSKEN
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.util.*

interface KjerneRegel : Regel


@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel(private val env: Environment) : KjerneRegel {
    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        sjekkGruppeRegel(
            { bruker.kreverGlobalGruppe(STRENGT_FORTROLIG) },
            ansatt,
            env.id(STRENGT_FORTROLIG)
        )

    override val metadata = RegelBeskrivelse(STRENGT_FORTROLIG_ADRESSE)


    @Component
    @Order(HIGHEST_PRECEDENCE + 1)
    class FortroligRegel(private val env: Environment) : KjerneRegel {
        override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
            sjekkGruppeRegel(
                { bruker.kreverGlobalGruppe(FORTROLIG) }, ansatt,
                env.id(FORTROLIG)
            )

        override val metadata = RegelBeskrivelse(FORTROLIG_ADRESSE)
    }

    @Component
    @Order(HIGHEST_PRECEDENCE + 2)
    class EgenAnsattRegel(private val env: Environment) : KjerneRegel {
        override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
            sjekkGruppeRegel(
                { bruker.kreverGlobalGruppe(EGEN_ANSATT) }, ansatt,
                env.id(EGEN_ANSATT)
            )

        override val metadata = RegelBeskrivelse(SKJERMING)
    }

    @Order(HIGHEST_PRECEDENCE + 3)
    @Component
    class EgneDataRegel(private val teller: EgneDataOppslagTeller) : KjerneRegel {
        override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
            avslåHvis({ ansatt er bruker }, teller)

        override val metadata = RegelBeskrivelse(EGNEDATA)
    }

    @Order(HIGHEST_PRECEDENCE + 4)
    @Component
    class ForeldreOgBarnRegel(private val teller: ForeldreBarnOppslagTeller) : KjerneRegel {
        override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
            avslåHvis({ ansatt erForeldreEllerBarnTil bruker }, teller)

        override val metadata = RegelBeskrivelse(FORELDREBARN)
    }

    @Order(HIGHEST_PRECEDENCE + 5)
    @Component
    class PartnerRegel(private val teller: PartnerOppslagTeller) : KjerneRegel {
        override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
            avslåHvis({ ansatt erNåværendeEllerTidligerePartnerTil bruker }, teller)

        override val metadata = RegelBeskrivelse(PARTNER)
    }

    @Component
    @Order(HIGHEST_PRECEDENCE + 6)
    class SøskenRegel(private val teller: SøskenOppslagTeller) : KjerneRegel {
        override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
            avslåHvis({ ansatt erSøskenTil bruker }, teller)

        override val metadata = RegelBeskrivelse(SØSKEN)
    }

}

fun Environment.id(gruppe: GlobalGruppe) = UUID.fromString(getRequiredProperty(gruppe.property))
