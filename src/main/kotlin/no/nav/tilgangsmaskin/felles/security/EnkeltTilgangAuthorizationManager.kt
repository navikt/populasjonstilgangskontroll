package no.nav.tilgangsmaskin.felles.security

import no.nav.tilgangsmaskin.felles.rest.Token.Companion.AZP_NAME
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.authorization.AuthorizationResult
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value
import java.util.function.Supplier

@Component
class EnkeltTilgangAuthorizationManager(
    @param:Value("\${overstyring.systemer:histark,gosys}")
    private val systemer: Set<String>
) : AuthorizationManager<RequestAuthorizationContext> {
    override fun authorize(authentication: Supplier<out Authentication>, context: RequestAuthorizationContext): AuthorizationResult {
        if (!isProd) return AuthorizationDecision(true)
        val jwt = authentication.get().principal as? Jwt ?: return AuthorizationDecision(false)
        val konsument = jwt.getClaimAsString(AZP_NAME).orEmpty().substringAfterLast(':')
        return AuthorizationDecision(konsument in systemer)
    }
}
