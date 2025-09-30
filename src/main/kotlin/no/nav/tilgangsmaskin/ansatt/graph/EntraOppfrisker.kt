package no.nav.tilgangsmaskin.ansatt.graph

import java.util.UUID
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelMapper.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.cache.CacheOppfrisker
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class EntraOppfrisker(private val oidTjeneste: AnsattOidTjeneste) : CacheOppfrisker {

    override val cacheName: String = GRAPH
    private val log = getLogger(javaClass)

    override fun oppfrisk(elementer: CacheNøkkelElementer) {
        runCatching {
            log.info("Oppfrisking ${elementer.nøkkel}")
            val ansattId = AnsattId(elementer.id)
            valider(elementer).call(this,ansattId, oidTjeneste.oidFraEntra(ansattId)).also {
                log.info("Oppfrisking ${elementer.nøkkel} OK")
            }
        }.getOrElse {
            log.info("Oppfrisking av ${elementer.nøkkel} etter sletting feilet, dette er ikke kritisk",it)
        }
    }
    private fun valider(deler: CacheNøkkelElementer)  =
        EntraTjeneste::class.members.first { it.name == deler.metode }
            .also {
                val params = it.parameters.drop(1)
                require(params[0].type.classifier == AnsattId::class) { "Argument 1 er ikke AnsattId" }
                require(params[1].type.classifier == UUID::class) { "Argument 2 er ikke UUID" }
            }

}