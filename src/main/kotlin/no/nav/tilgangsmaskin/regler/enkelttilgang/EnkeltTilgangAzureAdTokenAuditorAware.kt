package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.*


@Component
class EnkeltTilgangAzureAdTokenAuditorAware(private val token: Token) : AuditorAware<String> {
    override fun getCurrentAuditor() = Optional.of(token.ansattId?.verdi ?: UTILGJENGELIG)
}
