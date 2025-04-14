package no.nav.tilgangsmaskin.ansatt.entra

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.springframework.stereotype.Component

@Component
class OidResolver(private val adapter: EntraRestClientAdapter, private val accessor: TokenClaimsAccessor) {
    fun oidForAnsatt(ansattId: AnsattId) = oidFraToken() ?: oidFraEntra(ansattId.verdi)
    private fun oidFraToken() = runCatching {
        accessor.oidFraToken
    }.getOrNull()

    private fun oidFraEntra(ansattId: String) = adapter.oidFraEntra(ansattId)
}