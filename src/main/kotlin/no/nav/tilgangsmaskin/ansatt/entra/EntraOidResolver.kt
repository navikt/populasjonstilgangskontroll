package no.nav.tilgangsmaskin.ansatt.entra

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component

@Component
class EntraOidResolver(private val adapter: EntraRestClientAdapter, private val accessor: Token) {
    fun oidForAnsatt(ansattId: AnsattId) = accessor.oid ?: oidFraEntra(ansattId.verdi)

    private fun oidFraEntra(ansattId: String) = adapter.oidFraEntra(ansattId)
}