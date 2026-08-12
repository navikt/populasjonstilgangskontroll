package no.nav.tilgangsmaskin.felles.security

import no.nav.boot.conditionals.ConditionalOnProd
import no.nav.tilgangsmaskin.felles.rest.Token.Companion.AZP_NAME
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Fallback
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
@ConditionalOnProd
class StrictEnkeltTilgangAuthorizationManager(
    @param:Value("\${overstyring.systemer:histark,gosys}")
    private val systemer: Set<String>
) : AuthorizationManager<RequestAuthorizationContext> {
    override fun authorize(authentication: Supplier<out Authentication>, context: RequestAuthorizationContext) =
        AuthorizationDecision(authentication.get().systemNavn in systemer)

    private val Authentication.systemNavn
        get() = (principal as? Jwt)
            ?.getClaimAsString(AZP_NAME)
            ?.split(":")
            ?.lastOrNull()
}

@Component
@Fallback
class NoOpEnkeltTilgangAuthorizationManager : AuthorizationManager<RequestAuthorizationContext> {
    override fun authorize(authentication: Supplier<out Authentication>, context: RequestAuthorizationContext) =
        AuthorizationDecision(true)
}
