package no.nav.tilgangsmaskin.regler.overstyring

import no.nav.boot.conditionals.EnvUtil
import no.nav.boot.conditionals.EnvUtil.isProd
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class OverstyringClientValidator(private val cfg: OverstyringConfig, private val token: Token, private val env: Environment)  {
    private val log = getLogger(javaClass)

    fun validerKlient() {
        if (!cfg.systemer.contains(token.systemNavn) && isProd(env)) {
           throw OverstyringKlientException("System ${token.systemNavn} har ikke tilgang til overstyring, kun ${cfg.systemer.joinToString(", ")}",token.systemNavn)
        }
    }
    class OverstyringKlientException(message: String, val system: String) : RuntimeException(message)

}



