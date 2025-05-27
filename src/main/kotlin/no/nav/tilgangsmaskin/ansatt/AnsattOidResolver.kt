package no.nav.tilgangsmaskin.ansatt

import no.nav.tilgangsmaskin.ansatt.graph.EntraRestClientAdapter
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component

@Component
class AnsattOidResolver(private val adapter: EntraRestClientAdapter, private val token: Token) {
    fun oidForAnsatt(ansattId: AnsattId) = token.oid ?: oidFraEntra(ansattId.verdi)

    private fun oidFraEntra(ansattId: String) = adapter.oidFraEntra(ansattId)
}