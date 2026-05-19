package no.nav.tilgangsmaskin.ansatt.graph.oid

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidClient.Companion.filter
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.ENTRA_OID
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidRespons.EntraOid
import no.nav.tilgangsmaskin.felles.rest.IrrecoverableRestException
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Component
import java.util.*

@Component
class EntraOidTjeneste(private val oidClient: EntraOidClient, private val cfg: EntraOidConfig) {

    @Cacheable(cacheNames = [ENTRA_OID], key = "#ansattId.verdi")
    fun oid(ansattId: AnsattId) =
        validerRespons(ansattId, oidClient.oid(filter(ansattId)).oids)

    private fun validerRespons(ansattId: AnsattId, oids: Set<EntraOid>): UUID {
        return when (oids.size) {
            0 -> throw IrrecoverableRestException(NOT_FOUND, cfg.baseUri, "Fant ingen oid for $ansattId")
            1 -> oids.single().id
            else -> throw IrrecoverableRestException(CONFLICT, cfg.baseUri,
                "Forventet kun én oid for $ansattId, fant ${oids.size} (${oids.formattert()})")
        }
    }
}