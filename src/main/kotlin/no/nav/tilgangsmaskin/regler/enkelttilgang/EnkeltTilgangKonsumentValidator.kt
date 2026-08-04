package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.boot.conditionals.ConditionalOnProd
import org.springframework.context.annotation.Fallback
import org.springframework.stereotype.Component

@Component
@ConditionalOnProd
class EnkeltTilgangProdKonsumentValidator(private val cfg: EnkeltTilgangConfig) : EnkeltTilgangKonsumentValidator {
    override fun valider(konsument: String) {
        if (!cfg.systemer.contains(konsument)) {
            throw EnkeltTilgangKonsumentException("Konsument $konsument har ikke tilgang til enkelttilgang, kun ${
                cfg.systemer.joinToString(", ")
            }")
        }
    }
}

@Component
@Fallback
class EnkeltTilgangDevKonsumentValidator : EnkeltTilgangKonsumentValidator

interface EnkeltTilgangKonsumentValidator {
    fun valider(konsument: String) = Unit
}

class EnkeltTilgangKonsumentException(message: String) : RuntimeException(message)
