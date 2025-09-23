package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.felles.rest.cache.DetaljerFraKey
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste, private val oid: AnsattOidTjeneste) {
    private val log = getLogger(javaClass)

    fun oppfrisk(detaljer: DetaljerFraKey, ansattId: AnsattId) {
       with(detaljer) {
           val oid = oid.oidFraEntra(ansattId)
           if (metode == "geoGrupper") {
               entra.geoGrupper(ansattId, oid)
           }
           if (metode == "geoOgGlobaleGrupper") {
               entra.geoOgGlobaleGrupper(ansattId, oid)
           }
           log.trace("Oppfrisket $cacheName::$metode for ${ansattId.verdi} etter sletting" )
       }
    }
}