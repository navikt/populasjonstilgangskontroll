package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenClaimsAccessor
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.*


@Component
class OverstyringAzureAdTokenAuditorAware(private val accessor: TokenClaimsAccessor): AuditorAware<String> {
    override fun getCurrentAuditor() =
        runCatching {
            accessor.ansattId.let {  Optional.of(it.verdi) }
        }.getOrElse { Optional.of("N/A")}

}