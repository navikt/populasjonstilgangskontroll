package no.nav.tilgangsmaskin.regler.overstyring

import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class OverstyringClientValidator(private val cfg: OverstyringConfig, private val token: Token)  {
    private val log = getLogger(javaClass)

    fun validerKlient() {
        if (!cfg.systemer.contains(token.systemNavn)) {
            log.warn("System ${token.systemNavn} har ikke tilgang til overstyring, kun ${cfg.systemer.joinToString(", ")}")
           // throw IllegalStateException("System ${token.systemNavn} har ikke tilgang til overstyring, kun ${cfg.systemer.joinToString(", ")}")
        }
    }
    class OverstyringKlientException(message: String, val system: String) : RuntimeException(message)

}



