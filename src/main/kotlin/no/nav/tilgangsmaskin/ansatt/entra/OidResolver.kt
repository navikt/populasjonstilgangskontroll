package no.nav.tilgangsmaskin.ansatt.entra

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.springframework.stereotype.Component

@Component
class OidResolver(private val adapter: EntraRestClientAdapter, private val accessor: TokenClaimsAccessor) {
    fun oidForAnsatt(ansattId: AnsattId) = accessor.oid ?: oidFraEntra(ansattId.verdi)

    private fun oidFraEntra(ansattId: String) = adapter.oidFraEntra(ansattId)
}