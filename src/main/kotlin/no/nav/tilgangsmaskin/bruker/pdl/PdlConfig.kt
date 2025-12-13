package no.nav.tilgangsmaskin.bruker.pdl

import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.PdlRestClientAdapter.Companion.MED_FAMILIE
import no.nav.tilgangsmaskin.bruker.pdl.PdlRestClientAdapter.Companion.MED_UTVIDET_FAMILIE
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.rest.AbstractRestConfig
import no.nav.tilgangsmaskin.felles.rest.CachableRestConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI


@ConfigurationProperties(PDL)
class PdlConfig(
    baseUri: URI,
    pingPath: String = DEFAULT_PING_PATH,
    personPath: String = DEFAULT_PERSON_PATH,
    personBolkPath: String = DEFAULT_PERSON__BOLK_PATH,
    enabled: Boolean = true) : CachableRestConfig, AbstractRestConfig(baseUri, pingPath, PDL, enabled) {

    override val caches = PDL_CACHES
    override val navn = name
    override fun toString() = "$javaClass.simpleName [baseUri=$baseUri, pingEndpoint=$pingEndpoint]"

    val personURI = uri(personPath)
    val personerURI = uri(personBolkPath)

    companion object {
        const val PDL = "pdl"
        private const val DEFAULT_PING_PATH = "/internal/health/liveness"
        private const val DEFAULT_PERSON_PATH = "/api/v1/person"
        private const val DEFAULT_PERSON__BOLK_PATH = "/api/v1/personBolk"
        val  PDL_MED_FAMILIE_CACHE = CachableConfig(PDL,MED_FAMILIE)
        val  PDL_MED_UTVIDET_FAMILIE_CACHE = CachableConfig(PDL,MED_UTVIDET_FAMILIE)
        val PDL_CACHES  = listOf(PDL_MED_FAMILIE_CACHE,PDL_MED_UTVIDET_FAMILIE_CACHE)

    }
}

