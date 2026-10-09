package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.sikkerhetstjenesten.felles.security.AuthContext
import no.nav.sikkerhetstjenesten.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.Optional


@Component
class EnkeltTilgangAzureAdTokenAuditorAware(private val authContext: AuthContext) : AuditorAware<String> {
    override fun getCurrentAuditor() = Optional.of(authContext.navIdent ?: UTILGJENGELIG)
}
