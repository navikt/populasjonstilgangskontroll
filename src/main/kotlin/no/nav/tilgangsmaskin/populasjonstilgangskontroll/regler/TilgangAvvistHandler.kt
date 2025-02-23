package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.mask
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class TilgangAvvistHandler(private val overstyring: OverstyringTjeneste)  {

    private val log = getLogger(TilgangAvvistHandler::class.java)

    fun håndter(ansattId: AnsattId, brukerId: BrukerId, e: Throwable) =
        when (e) {
            is RegelException ->
                with(e.regel) {
                    log.trace("Sjekker om regler er overstyrt for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                    if (erOverstyrbar) {
                        if (overstyring.erOverstyrt(ansattId, brukerId)) {
                            log.warn("Overstyrt tilgang er gitt til ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'")
                        }
                        else {
                            throw e.also { log.warn("Ingen overstyring, tilgang avvist av regel '${beskrivelse.kortNavn}' for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}' består") }
                        }
                    } else {
                        throw e.also { log.trace("Tilgang avvist av kjerneregel '${beskrivelse.kortNavn}' for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}', avvisining består") }
                    }
                }
            else -> throw e.also { log.error("Ukjent feil ved tilgangskontroll for ansatt '${ansattId.verdi}' og bruker '${brukerId.mask()}'", it) }
        }
}