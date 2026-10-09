package no.nav.tilgangsmaskin.felles.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.sikkerhetstjenesten.felles.security.AbstractOAuth2JsonAccessDeniedHandler
import no.nav.sikkerhetstjenesten.felles.security.AuthContext
import no.nav.tilgangsmaskin.regler.enkelttilgang.ENKELTTILGANG_PATH
import no.nav.tilgangsmaskin.regler.motor.RegelMetadata.Companion.TYPE_URI
import org.slf4j.LoggerFactory
import tools.jackson.databind.json.JsonMapper

class OAuth2JsonAccessDeniedHandler(mapper: JsonMapper, authContext: AuthContext) :
    AbstractOAuth2JsonAccessDeniedHandler(mapper, authContext, TYPE_URI) {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun preHandle(req: HttpServletRequest, res: HttpServletResponse) {
        if (req.requestURI == ENKELTTILGANG_PATH) {
            log.info("Enkelttilgang avvist, ${authContext.navIdent} er ikke medlem av GA-Enkelttilgang")
        }
    }
}