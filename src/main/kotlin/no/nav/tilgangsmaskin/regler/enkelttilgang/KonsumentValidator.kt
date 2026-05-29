package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.boot.conditionals.ConditionalOnProd
import org.springframework.context.annotation.Fallback
import org.springframework.stereotype.Component

@Component
@ConditionalOnProd
class EnkeltTilgangProdClientValidator(private val cfg: EnkeltTilgangConfig) : KonsumentValidator {
    override fun valider(konsument: String) {
        if (!cfg.systemer.contains(konsument)) {
            throw EnkeltTilgangException("Konsument $konsument har ikke tilgang til enkelttilgang, kun ${cfg.systemer.joinToString(", ")}")
        }
    }
}

@Component
@Fallback
class EnkeltTilgangDevClientValidator : KonsumentValidator {
    override fun valider(konsument: String) = Unit
}

interface KonsumentValidator {
    fun valider(konsument: String)
}

class EnkeltTilgangException(message: String) : RuntimeException(message)
