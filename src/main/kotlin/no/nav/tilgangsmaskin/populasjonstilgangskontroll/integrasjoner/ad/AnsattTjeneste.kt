package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.MSGraphConfig.Companion.GRAPH
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
@Cacheable(GRAPH)
class AnsattTjeneste(private val adapter: MSRestClientAdapter) {

    fun saksbehandler(ident: NavId) : Saksbehandler {
       // val uuid = adapter.uuidForIdent(ident.verdi)
        //val tilganger = adapter.grupperForUUID(uuid)
        return Saksbehandler(ident, UUID.randomUUID(),emptyList())
    }

    fun ansattAzureId(ident: NavId) = adapter.uuidForIdent(ident.verdi)

    fun ansattTilganger(azureIdent: UUID) = adapter.grupperForUUID(azureIdent)
}

