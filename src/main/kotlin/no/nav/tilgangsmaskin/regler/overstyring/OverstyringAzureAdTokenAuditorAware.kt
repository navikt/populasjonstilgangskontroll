package no.nav.tilgangsmaskin.regler.overstyring

import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.*


@Component
class OverstyringAzureAdTokenAuditorAware(private val accessor: TokenClaimsAccessor) : AuditorAware<String> {
    override fun getCurrentAuditor() =
        accessor.ansattId?.let { Optional.of(it.verdi) } ?: Optional.of("N/A")
}