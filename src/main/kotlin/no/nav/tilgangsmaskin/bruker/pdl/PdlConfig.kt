package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.pdl.PdlPipClient.Companion.PDL_PIP_PERSONER_PATH
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipClient.Companion.PDL_PIP_PERSON_PATH
import no.nav.tilgangsmaskin.bruker.pdl.PdlPipClient.Companion.PDL_PIP_PING_PATH
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.cache.CacheNøkkelConfig
import no.nav.tilgangsmaskin.felles.rest.RestConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI.create


@Component
class PdlConfig(@Value("\${PDL}") hostname: String
) : CachableRestConfig, RestConfig(create(
    "https://$hostname"), PDL_PIP_PING_PATH, PDL) {

    override val caches = PDL_CACHES
    override val navn = name

    val personURI = uri(PDL_PIP_PERSON_PATH)
    val personerURI = uri(PDL_PIP_PERSONER_PATH)

    @Generated
    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    companion object {
        const val PDL = "pdl"
        val MED_FAMILIE = "medFamilie"
        val MED_UTVIDET_FAMILIE = "medUtvidetFamilie"
        val  PDL_MED_FAMILIE_CACHE = CacheNøkkelConfig(PDL,MED_FAMILIE)
        val  PDL_MED_UTVIDET_FAMILIE_CACHE = CacheNøkkelConfig(PDL,MED_UTVIDET_FAMILIE)
        val PDL_CACHES  = setOf(PDL_MED_FAMILIE_CACHE,PDL_MED_UTVIDET_FAMILIE_CACHE)

    }
}

