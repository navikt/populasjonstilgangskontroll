package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenAccessor
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.Optional


@Component
class OverstyringAzureAdTokenAuditorAware(private val accessor: TokenAccessor): AuditorAware<String> {
    override fun getCurrentAuditor() =
        runCatching {
            accessor.ansattId.let {  Optional.of(it.verdi) }
        }.getOrElse { Optional.of("N/A")}

}