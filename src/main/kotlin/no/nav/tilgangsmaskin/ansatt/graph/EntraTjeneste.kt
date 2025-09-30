package no.nav.tilgangsmaskin.ansatt.graph

import io.micrometer.core.annotation.Timed
import io.opentelemetry.instrumentation.annotations.WithSpan
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.RetryingOnRecoverableService
import org.springframework.cache.annotation.Cacheable
import java.util.UUID
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.cache.CacheOppfrisker
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import kotlin.reflect.KCallable

@RetryingOnRecoverableService
@Timed(value = "entra", histogram = true)
class EntraTjeneste(private val adapter: EntraRestClientAdapter, private val resolver: AnsattOidTjeneste) : CacheOppfrisker {

    override val cacheName: String = GRAPH
    private val log = getLogger(javaClass)

    @Cacheable(cacheNames = [GRAPH],  key = "#root.methodName + ':' + #ansattId.verdi")
    @WithSpan
    fun geoOgGlobaleGrupper(ansattId: AnsattId, oid: UUID) = adapter.grupper(oid.toString(), true)

    @Cacheable(cacheNames = [GRAPH],  key = "#root.methodName + ':' + #ansattId.verdi")
    @WithSpan
    fun geoGrupper(ansattId: AnsattId, oid: UUID) = adapter.grupper(oid.toString(), false)



    override fun oppfrisk(deler: CacheNøkkelElementer) {
        runCatching {
            val ansattId = AnsattId(deler.id)
            valider(deler).call(this,ansattId, resolver.oidFraEntra(ansattId)).also {
                log.trace("Oppfrisket ${deler.id.maskFnr()} etter sletting")
            }
        }.getOrElse {
            log.info("Oppfrisking av ${deler.id.maskFnr()} etter sletting feilet, dette er ikke kritisk",it)
        }
    }
     private fun valider(deler: CacheNøkkelElementer)  =
        EntraTjeneste::class.members.first { it.name == deler.metode }
            .also {
                val params = it.parameters.drop(1)
                require(params[0].type.classifier == AnsattId::class) { "Argument 1 er ikke AnsattId" }
                require(params[1].type.classifier == UUID::class) { "Argument 2 er ikke UUID" }
            }
    override fun toString() = "${javaClass.simpleName} [adapter=$adapter resolver=$resolver]"

}
