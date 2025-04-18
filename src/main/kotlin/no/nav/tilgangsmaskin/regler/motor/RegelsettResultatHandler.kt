package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.RegelSett.RegelType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@Counted
class RegelsettResultatHandler {
    private val log = LoggerFactory.getLogger(javaClass)
    fun avvist(
        pos: String,
        ansattId: AnsattId,
        brukerId: BrukerId,
        regel: Regel
    ) {
        log.warn("[#$pos] Tilgang avvist av regel '${regel.kortNavn}' (${regel.avvisningTekst}) for $ansattId og $brukerId")
    }

    fun ok(type: RegelType, ansattId: AnsattId, brukerId: BrukerId) {
        log.info("${type.beskrivelse.replaceFirstChar { it.uppercaseChar() }} ga tilgang OK for $ansattId og $brukerId")
    }
}