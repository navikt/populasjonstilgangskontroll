package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.ansatt.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.tilgang.TokenClaimsAccessor
import org.springframework.stereotype.Component

@Component
class OIDResolver(private val adapter: EntraClientAdapter, private val accessor: TokenClaimsAccessor) {
    fun oidForAnsatt(ansattId: AnsattId) = oidFraToken() ?: oidFraEntra(ansattId.verdi)
    private fun oidFraToken() = runCatching { accessor.oidFraToken }.getOrNull()
    private fun oidFraEntra(ansattId: String) = adapter.oidFraEntra(ansattId)
}