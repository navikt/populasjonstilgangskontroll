package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Ansatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.entra.EntraConfig.Companion.GRAPH
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
@Cacheable(GRAPH)
class EntraTjeneste(private val adapter: EntraClientAdapter) {

    fun ansatt(ident: NavId) : Ansatt {
        val attributter = adapter.attributter(ident.verdi)
        val grupper = adapter.grupper("${attributter.id}")
        return Ansatt(attributter,*grupper)
    }
    override fun toString() = "${javaClass.simpleName} [adapter=$adapter]"
}

