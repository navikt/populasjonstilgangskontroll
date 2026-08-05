package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterUtils.Companion.isProd
import org.springframework.stereotype.Component

@Component
class EnkeltTilgangKonsumentValidator(private val cfg: EnkeltTilgangConfig) {

    fun valider(konsument: String) {
        if (isProd && konsument !in cfg.systemer) {
            throw EnkeltTilgangKonsumentException("Konsument $konsument har ikke tilgang til enkelttilgang, kun ${cfg.systemer.sorted().joinToString(", ")}")
        }
    }

    class EnkeltTilgangKonsumentException(message: String) : RuntimeException(message)
}
