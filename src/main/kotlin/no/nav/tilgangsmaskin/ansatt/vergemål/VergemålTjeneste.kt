package no.nav.tilgangsmaskin.ansatt.vergemål

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyRestClientAdapter
import no.nav.tilgangsmaskin.ansatt.nom.NomConfig.Companion.NOM
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.ansatt.vergemål.VergemålConfig.Companion.VERGEMÅL
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverable
import org.slf4j.LoggerFactory.getLogger
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@RetryingWhenRecoverable
@Service
class VergemålTjeneste( private val nom: NomTjeneste,private val adapter: VergemålRestClientAdapter)  {


    @WithSpan
    @Cacheable(cacheNames = [VERGEMÅL],  key = "#ansattId.verdi")
    fun vergemål(ansattId: AnsattId) =
        nom.fnrForAnsatt(ansattId)?.let { adapter.vergemål(it.verdi) } ?: emptySet()

    @Generated
    override fun toString() = "${javaClass.simpleName} [adapter=$adapter]"
}


