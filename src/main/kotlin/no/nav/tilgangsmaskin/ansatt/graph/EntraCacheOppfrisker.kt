package no.nav.tilgangsmaskin.ansatt.graph

import java.util.UUID
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.felles.rest.cache.CacheNøkkelDeler
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import kotlin.reflect.KCallable

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste, private val oid: AnsattOidTjeneste) {
    private val log = getLogger(javaClass)

    fun oppfrisk(deler: CacheNøkkelDeler, id: String) {
       with(deler) {
           val ansattId = AnsattId(id)
           val oid = oid.oidFraEntra(ansattId)
           with(validerMetode(deler)) {
               call(entra, ansattId, oid)
               log.trace("Oppfrisket $key med metode $name etter sletting")
           }
       }
    }
    private fun validerMetode(deler: CacheNøkkelDeler): KCallable<*> =
        EntraTjeneste::class.members.firstOrNull { it.name == deler.metode }
            ?.also { metode ->
                val params = metode.parameters.drop(1)
                require(params[0].type.classifier == AnsattId::class) { "Argument 1 er ikke AnsattId" }
                require(params[1].type.classifier == UUID::class) { "Argument 2 er ikke UUID" }
            }
            ?: error("Fant ikke metode ${deler.metode} i EntraTjeneste for oppfrisking av cache ${deler.cacheName}")
}