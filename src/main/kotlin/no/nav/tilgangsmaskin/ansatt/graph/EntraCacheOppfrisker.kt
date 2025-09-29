package no.nav.tilgangsmaskin.ansatt.graph

import java.util.UUID
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.rest.cache.CacheNøkkelDeler
import no.nav.tilgangsmaskin.felles.rest.cache.CacheOppfrisker
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste, private val oid: AnsattOidTjeneste) : CacheOppfrisker {
    private val log = getLogger(javaClass)

    override val cacheName = GRAPH

    override fun oppfrisk(deler: CacheNøkkelDeler) {
        runCatching {
            with(AnsattId(deler.id)) {
                valider(deler).call(entra,this, oid.oidFraEntra(this)).also {
                    log.trace(CONFIDENTIAL,"Oppfrisket ${deler.key} etter sletting")
                }
            }
        }.getOrElse {
            log.info("Oppfrisking av ${deler.id.maskFnr()} etter sletting feilet, dette er ukritisk",it)
        }
    }
    override fun valider(deler: CacheNøkkelDeler)  =
        EntraTjeneste::class.members.first { it.name == deler.metode }
            .also {
                val params = it.parameters.drop(1)
                require(params[0].type.classifier == AnsattId::class) { "Argument 1 er ikke AnsattId" }
                require(params[1].type.classifier == UUID::class) { "Argument 2 er ikke UUID" }
            }
}