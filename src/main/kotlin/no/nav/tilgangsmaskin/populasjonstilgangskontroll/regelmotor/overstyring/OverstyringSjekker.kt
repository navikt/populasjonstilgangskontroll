package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.BulkRegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.RegelException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OverstyringSjekker(private val overstyring: OverstyringTjeneste)  {

    private val log = LoggerFactory.getLogger(OverstyringSjekker::class.java)

    fun sjekk(ansattId: AnsattId, e: Throwable) =
        when (e) {
            is BulkRegelException -> sjekkOverstyringer(e, ansattId)
            is RegelException -> sjekkOverstyring(e, ansattId)
            else -> throw e.also { log.error("Ukjent feil ved tilgangskontroll for '${ansattId.verdi}", it) }
        }

    private fun sjekkOverstyring(e: RegelException, ansattId: AnsattId) =
        with(e.regel) {
            log.trace("Sjekker om regler er overstyrt for ansatt '${ansattId.verdi}' og bruker '${e.brukerId.mask()}'")
            if (erOverstyrbar) {
                if (erOverstyrt(ansattId, e.brukerId)) {
                    log.warn("Overstyrt tilgang er gitt til ansatt '${ansattId.verdi}' og bruker '${e.brukerId.mask()}'")
                } else {
                    throw e.also { log.warn("Ingen overstyring, tilgang avvist av regel '${metadata.kortNavn}' for '${ansattId.verdi}' '${e.brukerId.mask()}' består") }
                }
            } else {
                throw e.also { log.trace("Tilgang avvist av kjerneregel '${metadata.kortNavn}' for '${ansattId.verdi}' og '${e.brukerId.mask()}', avvisining består") }
            }
    }

    private fun sjekkOverstyringer(e: BulkRegelException, ansattId: AnsattId) {
        with(e.exceptions.toMutableList()) {
            removeIf {
                it.regel.erOverstyrbar && erOverstyrt(ansattId, it.brukerId)
            }.also {
                if (it) {
                    log.info("Fjernet $${this.size - size} exception grunnet overstyrte regler for $ansattId")
                } else {
                    log.info("Ingen overstyrte regler for $ansattId")
                }
            }
            if (isNotEmpty()) {
                throw BulkRegelException(ansattId, this)
            }
        }
    }

    private fun erOverstyrt(ansattId: AnsattId, brukerId: BrukerId) = overstyring.erOverstyrt(ansattId, brukerId)
}