package no.nav.tilgangsmaskin.ansatt.graph.oid

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidClient.Companion.filter
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.ENTRA_OID
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidRespons.EntraOid
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class EntraOidTjeneste(private val oidClient: EntraOidClient) {


    @Cacheable(cacheNames = [ENTRA_OID], key = "#ansattId.verdi")
    fun oid(ansattId: AnsattId) =
        validerRespons(ansattId,oidClient.oid(filter(ansattId)).oids)

    private fun validerRespons(ansattId: AnsattId, oids: Set<EntraOid>) : UUID {
        return when (oids.size) {
            0 -> throw EntraUnexpectedResponseException(ansattId, "Fant ingen oid for $ansattId?", NOT_FOUND)
            1 -> oids.single().id
            else -> throw EntraUnexpectedResponseException(ansattId, "Forventet kub én oid for $ansattId, fant ${oids.size} (${oids.formattert()})", CONFLICT)
        }
    }
}