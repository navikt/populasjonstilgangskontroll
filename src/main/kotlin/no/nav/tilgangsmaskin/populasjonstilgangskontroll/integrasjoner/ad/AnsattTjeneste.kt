package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import org.springframework.stereotype.Service
import java.util.*

@Service
class AnsattTjeneste(private val adapter: MSRestClientAdapter) {

    fun ansattAzureId(ident: String) = adapter.hentUUIDforNavIdent(ident)

    fun ansattTilganger(azureIdent: UUID) = adapter.hentGrupperForNavIdent(azureIdent)
}