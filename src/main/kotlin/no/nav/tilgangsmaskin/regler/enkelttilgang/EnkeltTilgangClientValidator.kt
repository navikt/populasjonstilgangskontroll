package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.boot.conditionals.EnvUtil.isProd
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class EnkeltTilgangClientValidator(private val cfg: EnkeltTilgangConfig, private val token: Token, private val env: Environment) : KonsumentValidator {

    override fun valider() {
        if (!cfg.systemer.contains(token.systemNavn) && isProd(env)) {
           throw OverstyringException("System ${token.systemNavn} har ikke tilgang til overstyring, kun ${cfg.systemer.joinToString(", ")}",token.systemNavn)
        }
    }
    class OverstyringException(message: String, val system: String) : RuntimeException(message)

}

interface KonsumentValidator {
    fun valider()
}



