package no.nav.tilgangsmaskin.regler.overstyring

import no.nav.boot.conditionals.EnvUtil.isProd
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class OverstyringClientValidator(private val cfg: OverstyringConfig, private val token: Token, private val env: Environment)  {

    fun validerKonsument() {
        if (!cfg.systemer.contains(token.systemNavn) && isProd(env)) {
           throw OverstyringKlientException("System ${token.systemNavn} har ikke tilgang til overstyring, kun ${cfg.systemer.joinToString(", ")}",token.systemNavn)
        }
    }
    class OverstyringKlientException(message: String, val system: String) : RuntimeException(message)

}



