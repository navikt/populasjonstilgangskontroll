package no.nav.tilgangsmaskin.ansatt.graph

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.felles.rest.cache.DetaljerFraKey
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EntraCacheOppfrisker(private val entra: EntraTjeneste, private val oid: AnsattOidTjeneste) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun oppfrisk(detaljer: DetaljerFraKey, ansattId: AnsattId) {
        if (detaljer.metode == "geoGrupper") {
            entra.geoGrupper(ansattId, oid.oidFraEntra(ansattId))
            log.info("Oppfrisket ${detaljer.cacheName}::geoGrupper for ${ansattId.verdi} etter sletting" )
        }
        if (detaljer.metode == "geoOgGlobaleGrupper") {
            entra.geoOgGlobaleGrupper(ansattId, oid.oidFraEntra(ansattId))
            log.info("Oppfrisket ${detaljer.cacheName}::geoOgGlobaleGrupper  for ${ansattId.verdi} etter sletting" )
        }
    }
}