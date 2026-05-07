package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.pdl.PdlPipClient.Companion.PERSONER_PATH
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipClient.Companion.PERSON_PATH
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipClient.Companion.PING_PATH
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI.create


@Component
class PdlConfig(@Value("\${PDL}") hostname: String
) : CachableRestConfig, AbstractRestConfig(create(
    "https://$hostname"), PING_PATH, PDL) {

    override val caches = PDL_CACHES
    override val navn = name

    val personURI = uri(PERSON_PATH)
    val personerURI = uri(PERSONER_PATH)

    @Generated
    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val PDL = "pdl"
        val MED_FAMILIE = "medFamilie"
        val MED_UTVIDET_FAMILIE = "medUtvidetFamilie"
        private const val DEFAULT_PING_PATH = "/internal/health/liveness"
        val  PDL_MED_FAMILIE_CACHE = CachableConfig(PDL,MED_FAMILIE)
        val  PDL_MED_UTVIDET_FAMILIE_CACHE = CachableConfig(PDL,MED_UTVIDET_FAMILIE)
        val PDL_CACHES  = setOf(PDL_MED_FAMILIE_CACHE,PDL_MED_UTVIDET_FAMILIE_CACHE)

    }
}

