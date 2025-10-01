package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraConfig.Companion.GRAPH
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelMapper.CacheNøkkelElementer
import no.nav.tilgangsmaskin.felles.cache.CacheOppfrisker
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class EntraOppfrisker(private val entra: EntraTjeneste, private val oidTjeneste: AnsattOidTjeneste) : CacheOppfrisker{

    override val cacheName: String = GRAPH
    private val log = getLogger(javaClass)

    override fun oppfrisk(elementer: CacheNøkkelElementer) {
            runCatching {
                val ansattId = AnsattId(elementer.id)
                when (elementer.metode) {
                    "geoOgGlobaleGrupper" -> entra.geoOgGlobaleGrupper(ansattId, oidTjeneste.oidFraEntra(ansattId))
                    "geoGrupper" -> entra.geoGrupper(ansattId, oidTjeneste.oidFraEntra(ansattId))
                    else -> throw IllegalArgumentException("Ukjent metode ${elementer.metode} i nøkkel ${elementer.nøkkel}")
                }
                log.info("Oppfrisking av ${elementer.nøkkel} OK")
            }.getOrElse {
                log.info("Oppfrisking av ${elementer.nøkkel} etter sletting feilet, dette er ikke kritisk",it)
            }
        }
}