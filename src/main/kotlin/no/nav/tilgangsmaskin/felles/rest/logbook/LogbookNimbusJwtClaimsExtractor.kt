package no.nav.tilgangsmaskin.felles.rest.logbook

import com.nimbusds.jwt.SignedJWT
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER
import org.springframework.stereotype.Component
import org.zalando.logbook.HttpRequest
import org.zalando.logbook.attributes.AttributeExtractor
import org.zalando.logbook.attributes.HttpAttributes
import org.zalando.logbook.attributes.HttpAttributes.EMPTY

@Component
class LogbookNimbusJwtClaimsExtractor : AttributeExtractor {

    override fun extract(request: HttpRequest): HttpAttributes {
        val auth = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return EMPTY
        return HttpAttributes(SignedJWT.parse(auth.removePrefix(BEARER.value) + " ").jwtClaimsSet.claims.withTimestampsInCurrentTimezone())
    }
}