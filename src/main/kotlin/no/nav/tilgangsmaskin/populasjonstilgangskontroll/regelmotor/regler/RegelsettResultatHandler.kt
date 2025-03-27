package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@Counted
class RegelsettResultatHandler {
    private val log = LoggerFactory.getLogger(javaClass)
    fun avvist(pos: String, ansattId: AnsattId, brukerId: BrukerId, regel: Regel) {
        log.warn("[#$pos] Tilgang avvist av regel '${regel.kortNavn}' (${regel.avvisningTekst}) for $ansattId og $brukerId")
    }
    fun ok(type: RegelSett.RegelType, ansattId: AnsattId, brukerId: BrukerId) {
        log.info("${type.beskrivelse.replaceFirstChar { it.uppercaseChar() }} ga tilgang OK for $ansattId og $brukerId")
    }
}