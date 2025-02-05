package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Saksbehandler
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.MSGraphConfig.Companion.GRAPH
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
@Cacheable(GRAPH)
class EntraTjeneste(private val adapter: MSRestClientAdapter) {

    fun saksbehandler(ident: NavId) : Saksbehandler {
        val attributter = adapter.attributterForIdent(ident.verdi)
        val grupper = adapter.grupperForUUID(attributter.id)
        return Saksbehandler(attributter, grupper)
    }

    fun ansattAzureId(ident: NavId) = adapter.attributterForIdent(ident.verdi)

    fun ansattTilganger(azureIdent: UUID) = adapter.grupperForUUID(azureIdent)
}

