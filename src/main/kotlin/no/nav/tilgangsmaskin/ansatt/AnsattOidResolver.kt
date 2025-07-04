package no.nav.tilgangsmaskin.ansatt

import no.nav.tilgangsmaskin.ansatt.graph.EntraRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.time.Duration
import org.springframework.context.annotation.Lazy

@Component
class AnsattOidResolver(private val adapter: EntraRestClientAdapter, private val token: Token) :CachableRestConfig {

    private val log = getLogger(javaClass)

    @Autowired
    @Lazy
    lateinit var self: AnsattOidResolver

    fun oidForAnsatt(ansattId: AnsattId) =  if (token.erObo)  token.oid else self.oidFraEntra(ansattId.verdi)

    @Cacheable(cacheNames = ["entra-oid"])
     fun oidFraEntra(ansattId: String) = adapter.oidFraEntra(ansattId).also {
        log.debug("OID fra Entra for {} er {}", ansattId, it)
    }

    override val varighet = Duration.ofDays(365)  // Godt nok, blås i skuddår
    override val navn = "entra-oid"
}