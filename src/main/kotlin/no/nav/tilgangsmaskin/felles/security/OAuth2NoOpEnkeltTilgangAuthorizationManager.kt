package no.nav.tilgangsmaskin.felles.security

import no.nav.boot.conditionals.ConditionalOnProd
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Fallback
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
@ConditionalOnProd
class StrictEnkeltTilgangAuthorizationManager(
    @param:Value("\${overstyring.systemer:histark,gosys}")
    private val tillatteSystemer: Set<String>) : AuthorizationManager<RequestAuthorizationContext> {
    override fun authorize(authentication: Supplier<out Authentication>, context: RequestAuthorizationContext) =
        AuthorizationDecision(tillatteSystemer.any { tillattSystem ->
            authentication.get().authorities.any {
                it.authority == SystemAuthority(tillattSystem).authority
            }
        })
}

@Component
@Fallback
class NoOpEnkeltTilgangAuthorizationManager : AuthorizationManager<RequestAuthorizationContext> {
    override fun authorize(authentication: Supplier<out Authentication>, context: RequestAuthorizationContext) =
        AuthorizationDecision(true)
}
