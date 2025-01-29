package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.ad.MSGraphConfig.Companion.GRAPH
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
@Cacheable(GRAPH)
class AnsattTjeneste(private val adapter: MSRestClientAdapter) {

    fun ansattAzureId(ident: NavId) = adapter.hentUUIDforNavIdent(ident.verdi)

    fun ansattTilganger(azureIdent: UUID) = adapter.hentGrupperForNavIdent(azureIdent)
}

@JvmInline
value class NavId(val verdi: String) {
    init {
        require(verdi.length == 7) { "Ugyldig lengde på ident: $verdi" }
        require(verdi.first().isUpperCase()) { "Ugyldig første tegn i ident: $verdi, må være stor bokstav" }
    }
}
data class AdGruppe(val id: UUID, val name: String)
