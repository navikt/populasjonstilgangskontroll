package no.nav.tilgangsmaskin.bruker.pdl

import io.micrometer.core.instrument.MeterRegistry
import no.nav.person.pdl.leesah.Personhendelse
import no.nav.tilgangsmaskin.felles.kafka.TypedKafkaDroppedMessageMeter
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.springframework.stereotype.Component

@Component
class PdlDroppedMessageMeter(registry: MeterRegistry) :
    TypedKafkaDroppedMessageMeter<Personhendelse>(registry, Personhendelse::class) {

    override fun formatEvent(event: Personhendelse) =
        "gradering=${event.adressebeskyttelse?.gradering ?: "UGRADERT"}, " +
            "endringstype=${event.endringstype}, " +
            "identer=${event.personidenter.map { it.maskFnr() }}"
}

