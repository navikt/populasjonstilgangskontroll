package no.nav.tilgangsmaskin.populasjonstilgangskontroll.Tilgang

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.spring.UnprotectedRestController
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattOidTjeneste
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.graph.EntraTjeneste
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.Identifikator
import no.nav.tilgangsmaskin.bruker.pdl.PdlConfig.Companion.PDL
import no.nav.tilgangsmaskin.bruker.pdl.Person
import no.nav.tilgangsmaskin.felles.cache.CachableConfig
import no.nav.tilgangsmaskin.felles.cache.CacheClient
import no.nav.tilgangsmaskin.felles.cache.Caches
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody


@UnprotectedRestController(value = ["/${DEV}/ansatt/"])
@ConditionalOnNotProd
@Tag(name = "DevAnsattController", description = "Denne kontrolleren skal kun brukes til testing")
class DevAnsattController(
    private val ansatte: AnsattTjeneste,
    private val proxy: EntraProxyTjeneste,
    private val oid: AnsattOidTjeneste,
    private val entra: EntraTjeneste) {
    @GetMapping("{ansattId}")
    fun ansatt(@PathVariable ansattId: AnsattId) = ansatte.ansatt(ansattId)

    @GetMapping("proxy/{ansattId}")
    fun enhet(@PathVariable ansattId: AnsattId) = proxy.enhet(ansattId)

    @GetMapping("enheter/{ansattId}")
    fun enheter(@PathVariable ansattId: AnsattId) = entra.geoOgGlobaleGrupper(ansattId, oid.oidFraEntra(ansattId)).filter { it.displayName.contains("ENHET") }

}