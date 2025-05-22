package no.nav.tilgangsmaskin.ansatt.entra

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component

@Component
class EntraOidResolver(private val adapter: EntraRestClientAdapter, private val token: Token) {
    fun oidForAnsatt(ansattId: AnsattId) = token.oid ?: oidFraEntra(ansattId.verdi)

    private fun oidFraEntra(ansattId: String) = adapter.oidFraEntra(ansattId)
}
