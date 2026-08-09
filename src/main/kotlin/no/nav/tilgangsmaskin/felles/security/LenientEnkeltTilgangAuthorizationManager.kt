package no.nav.tilgangsmaskin.felles.security

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.boot.conditionals.ConditionalOnProd
import no.nav.tilgangsmaskin.felles.rest.Token
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.function.Supplier

interface EnkeltTilgangAuthorizationManager : AuthorizationManager<RequestAuthorizationContext>

@Component
@ConditionalOnProd
class StrictEnkeltTilgangAuthorizationManager(
    private val token: Token,
    @param:Value("\${overstyring.systemer:histark,gosys}")
    private val systemer: Set<String>
) : EnkeltTilgangAuthorizationManager {
    override fun authorize(authentication: Supplier<out Authentication>, context: RequestAuthorizationContext) =
        AuthorizationDecision(token.systemNavn in systemer)
}

@Component
@ConditionalOnNotProd
class LenientEnkeltTilgangAuthorizationManager : EnkeltTilgangAuthorizationManager {
    override fun authorize(authentication: Supplier<out Authentication>, context: RequestAuthorizationContext) =
        AuthorizationDecision(true)
}
