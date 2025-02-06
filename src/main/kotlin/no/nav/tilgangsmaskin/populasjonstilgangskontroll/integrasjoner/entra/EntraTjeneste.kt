package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
@Cacheable(GRAPH)
class EntraTjeneste(private val adapter: EntraClientAdapter) {


    fun saksbehandler(ident: NavId) : Saksbehandler {
        val attributter = adapter.attributterForIdent(ident.verdi)
        val grupper = adapter.grupperForUUID(attributter.id)
        return Saksbehandler(attributter, grupper).also {
            log.info("Saksbehandler: $it")
        }
    }

    fun ansattAzureId(ident: NavId) = adapter.attributterForIdent(ident.verdi)

    fun ansattTilganger(azureIdent: UUID) = adapter.grupperForUUID(azureIdent)
}

