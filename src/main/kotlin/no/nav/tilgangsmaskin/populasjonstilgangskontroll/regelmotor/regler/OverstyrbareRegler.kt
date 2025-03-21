package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AvvisningTekster.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.Bruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeoTilknytning.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.Regel.RegelBeskrivelse
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.månederSidenIdag
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenClaimsAccessor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*

@Component
@Order(LOWEST_PRECEDENCE)
class GeoNorgeRegel(@Value("\${gruppe.nasjonal}") private val id: UUID) : OverstyrbarRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        ansatt.kanBehandle(id) || ansatt.grupper.any { it.displayName.endsWith("GEO_${
            when (bruker.geoTilknytning) {
                is KommuneTilknytning -> bruker.geoTilknytning.kommune.verdi
                is BydelTilknytning -> bruker.geoTilknytning.bydel.verdi
                else -> return true
            }
        }")
        }

    override val metadata = RegelBeskrivelse("Geografisk tilknytning", AVVIST_GEOGRAFISK)
}

@Component
@Order(LOWEST_PRECEDENCE - 1)
class UkjentBostedGeoRegel(@Value("\${gruppe.udefinert}") private val id: UUID) : OverstyrbarRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        if (bruker.geoTilknytning is UkjentBosted) {
            ansatt.kanBehandle(id)
        } else true

    override val metadata = RegelBeskrivelse("Person bosatt ukjent bosted", AVVIST_PERSON_UKJENT)
}


@Component
@Order(LOWEST_PRECEDENCE - 2)
class UtlandUdefinertGeoRegel(@Value("\${gruppe.utland}") private val id: UUID) : OverstyrbarRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        if (bruker.geoTilknytning is UtenlandskTilknytning) {
            ansatt.kanBehandle(id)
        } else true

    override val metadata = RegelBeskrivelse("Person bosatt utland", AVVIST_PERSON_UTLAND)
}

@Component
@Order(LOWEST_PRECEDENCE - 3)
class AvdødBrukerRegel(private val teller: AvdødAksessTeller) : OverstyrbarRegel {
    override fun test(ansatt: Ansatt, bruker: Bruker) =
        bruker.dødsdato?.let {
            teller.avdødBrukerAksess(ansatt.ansattId, bruker.brukerId, it)
        } ?: true
    override val metadata = RegelBeskrivelse("Avdød bruker", AVVIST_AVDØD)
}

@Component
class AvdødAksessTeller(private val meterRegistry: MeterRegistry, private val accessor: TokenClaimsAccessor) {

    private val log = LoggerFactory.getLogger(javaClass)
    fun avdødBrukerAksess(ansattId: AnsattId, brukerId: BrukerId, dødsdato: LocalDate) =
        true.also {  // TODO Endre til false når vi faktisk skal håndtere døde
            Counter.builder("dead.attempted.total")
                .description("Number of deceased users attempted accessed")
                .tag("months",intervallFor(dødsdato))
                .tag("system",accessor.system)
                .register(meterRegistry).increment()
            log.warn("Ansatt ${ansattId.verdi} forsøkte å aksessere avdød bruker ${brukerId.mask()} fra ${accessor.system}")
        }

    private fun intervallFor(dato: LocalDate) =
        when (dato.månederSidenIdag()) {
            in 0..6 -> "0-6"
            in 7..12 -> "7-12"
            in 13..24 -> "13-24"
            else -> ">24"
        }
}





