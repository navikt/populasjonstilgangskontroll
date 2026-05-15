package no.nav.tilgangsmaskin.ansatt.graph.oid

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidClient.Companion.accountFilter
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.ENTRA_OID
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidRespons.EntraOid
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class EntraOidTjeneste(private val oidClient: EntraOidClient) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Cacheable(cacheNames = [ENTRA_OID], key = "#ansattId.verdi")
    fun oid(ansattId: AnsattId) = validerRespons(ansattId,oidClient.oid(accountFilter(ansattId)).oids)

    private fun validerRespons(ansattId: AnsattId, oids: Set<EntraOid>) : UUID {
        log.trace("Fant {} oids i Entra for {}", oids.size, ansattId)
        return when (oids.size) {
            0 -> throw EntraOidException(ansattId, "Fant ingen oid for $ansattId, er den fremdeles gyldig?")
            1 -> oids.single().id
            else -> throw EntraOidException(ansattId, "Forventet nøyaktig én oid for $ansattId, fant ${oids.size} (${oids.joinToString(", ") { it.id.toString() }})")
        }
    }
}