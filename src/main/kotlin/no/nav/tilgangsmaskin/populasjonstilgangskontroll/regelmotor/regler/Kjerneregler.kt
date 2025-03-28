package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AvvisningKode.AVVIST_HABILITET
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AvvisningKode.AVVIST_EGNE_DATA
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.GlobalGruppe.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

interface KjerneRegel : Regel


@Component
@Order(HIGHEST_PRECEDENCE)
class StrengtFortroligRegel(@Value("\${gruppe.strengt}") private val id: UUID) : KjerneRegel  {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        sjekkRegel({bruker.kreverGlobalGruppe(STRENGT_FORTROLIG_GRUPPE)}, bruker, ansatt, id)

        override val metadata = RegelBeskrivelse("Kode 6", STRENGT_FORTROLIG_GRUPPE.kode)
}

@Component
@Order(HIGHEST_PRECEDENCE + 1)
class FortroligRegel(@Value("\${gruppe.fortrolig}") private val id: UUID): KjerneRegel {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        sjekkRegel({bruker.kreverGlobalGruppe(FORTROLIG_GRUPPE)}, bruker, ansatt, id)

    override val metadata = RegelBeskrivelse("Kode 7", FORTROLIG_GRUPPE.kode)
}

@Component
@Order(HIGHEST_PRECEDENCE + 2)
class EgenAnsattRegel(@Value("\${gruppe.egenansatt}") private val id: UUID) : KjerneRegel {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        sjekkRegel({bruker.kreverGlobalGruppe(EGEN_ANSATT_GRUPPE)}, bruker, ansatt, id)

    override val metadata = RegelBeskrivelse("Kode 8", EGEN_ANSATT_GRUPPE.kode)
}

@Order(HIGHEST_PRECEDENCE + 3)
@Component
class EgneDataRegel : KjerneRegel {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        bruker.brukerId != ansatt.brukerId

    override val metadata = RegelBeskrivelse("Egne data", AVVIST_EGNE_DATA)
}

@Order(HIGHEST_PRECEDENCE + 4)
@Component
class ForeldreOgBarnRegel : KjerneRegel {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        !(ansatt erForeldreEllerBarnTil bruker)

    override val metadata = RegelBeskrivelse("Oppslag med manglende habilitet", AVVIST_HABILITET)
}

@Component
@Order(HIGHEST_PRECEDENCE + 5)
class SøskenRegel(private val teller: SøskenOppslagTeller) : KjerneRegel {
    override fun erOK(ansatt: Ansatt, bruker: Bruker) =
        if (ansatt erSøskenTil bruker) {
            teller.registrerOppslag(ansatt.ansattId, bruker.brukerId)
        } else true

    override val metadata = RegelBeskrivelse("Oppslag habilitet", AVVIST_HABILITET)
}