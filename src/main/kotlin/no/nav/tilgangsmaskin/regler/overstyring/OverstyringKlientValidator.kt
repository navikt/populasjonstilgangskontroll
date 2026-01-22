package no.nav.tilgangsmaskin.regler.overstyring

import io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringConfig.Companion.OVERSTYRING
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import java.net.URI

@Component
class OverstyringKlientValidator(private val cfg: OverstyringConfig, private val token: Token)  {
    private val log = getLogger(javaClass)

    fun validerKlient() {
        if (!cfg.systemer.contains(token.systemNavn)) {
            log.warn("System ${token.systemNavn} har ikke tilgang til overstyring, kun ${cfg.systemer.joinToString(", ")}")
           // throw IllegalStateException("System ${token.systemNavn} har ikke tilgang til overstyring, kun ${cfg.systemer.joinToString(", ")}")
        }
    }
}

class OverstyringAvvistException(message: String, uri: URI) : IrrecoverableRestException( HttpStatusCode.valueOf(BAD_REQUEST.code()),uri,message)


@ConfigurationProperties(OVERSTYRING)
class OverstyringConfig(val systemer: Set<String> = setOf("histark","gosys")) {

    companion object {
        const val OVERSTYRING = "overstyring"
    }
}