package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.tilgangsmaskin.felles.security.AuthContext
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.*


@Component
class EnkeltTilgangAzureAdTokenAuditorAware(private val authContext: AuthContext) : AuditorAware<String> {
    override fun getCurrentAuditor() = Optional.of(authContext.ansattId?.verdi ?: UTILGJENGELIG)
}
