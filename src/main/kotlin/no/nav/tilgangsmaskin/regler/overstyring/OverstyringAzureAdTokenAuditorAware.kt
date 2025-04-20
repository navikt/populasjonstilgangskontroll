package no.nav.tilgangsmaskin.regler.overstyring

import java.util.*
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component


@Component
class OverstyringAzureAdTokenAuditorAware(private val accessor: TokenClaimsAccessor) : AuditorAware<String> {
    override fun getCurrentAuditor() = Optional.ofNullable(accessor.ansattId?.verdi ?: "N/A")
}
