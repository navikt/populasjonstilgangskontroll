package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler

import io.micrometer.core.annotation.Counted
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.tilgang1.TokenClaimsAccessor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SøskenAksessTeller(private val accessor: TokenClaimsAccessor) {
    private val log = LoggerFactory.getLogger(javaClass)
    @Counted
    fun registrerAksess(ansattId: AnsattId, brukerId: BrukerId) =
        true.also {
            log.warn("Ansatt $ansattId aksesserte søsken $brukerId fra system ${accessor.systemNavn}")
        }
}