package no.nav.tilgangsmaskin.ansatt.graph.oid

import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.graph.oid.EntraOidConfig.Companion.ENTRA_OID
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
class EntraOidTjeneste(private val oidClient: EntraOidClient) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Cacheable(cacheNames = [ENTRA_OID], key = "#ansattId.verdi")
    fun oid(ansattId: AnsattId) = resolveOid(ansattId.verdi)

    private fun resolveOid(ansattId: String) =
        oidClient.oid(EntraOidClient.accountFilter(ansattId)).oids
            .also { log.trace("Fant ${it.size} oids i Entra for $ansattId") }
            .let { oids ->
                when (oids.size) {
                    0 -> throw EntraOidException(ansattId, "Fant ingen oid for $ansattId, er den fremdeles gyldig?")
                    1 -> oids.single().id
                    else -> throw EntraOidException(ansattId, "Forventet nøyaktig én oid for $ansattId, fant ${oids.size} (${oids.joinToString(", ") { it.id.toString() }})")
                }
            }
}