package no.nav.tilgangsmaskin.ansatt.vergemål

import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyRestClientAdapter
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.RetryingWhenRecoverable
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service

@RetryingWhenRecoverable
@Service
class VergemålTjeneste(private val adapter: VergemålRestClientAdapter)  {


    @WithSpan
    fun vergemål(ansatt: BrukerId?) =
        ansatt?.let { adapter.vergemål(it.verdi) }

    @Generated
    override fun toString() = "${javaClass.simpleName} [adapter=$adapter]"
}


