package no.nav.tilgangsmaskin.ansatt.graph

import java.lang.IllegalStateException
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.felles.rest.cache.CacheNøkkelDeler
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste, private val oid: AnsattOidTjeneste) {
    private val log = getLogger(javaClass)

    fun oppfrisk(deler: CacheNøkkelDeler, id: String) {
       with(deler) {
           val ansattId = AnsattId(id)
           val oid = oid.oidFraEntra(ansattId)
           val method = EntraTjeneste::class.members.firstOrNull { it.name == metode }
           method?.call(entra, ansattId, oid).also {
               log.trace("Oppfrisket $key med metode ${method?.name}  etter sletting" )
           } ?: throw IllegalStateException("Fant ikke metode $metode for oppfrisking av cache")
       }
    }
}