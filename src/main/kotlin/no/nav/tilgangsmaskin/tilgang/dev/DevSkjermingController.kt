package no.nav.tilgangsmaskin.populasjonstilgangskontroll.Tilgang

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingRestClientAdapter
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.DEV
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/${DEV}/skjermning")
@ConditionalOnNotProd
@Tag(name = "DevTilgangController", description = "Denne kontrolleren skal kun brukes til testing")
class DevSkjermingController(
    private val skjerming: SkjermingTjeneste,
    private val skjermingAdapter: SkjermingRestClientAdapter) {


    @PostMapping("skjermingadaptere")
    fun skjermingAdapter(@RequestBody brukerId: String) = skjermingAdapter.skjerming(brukerId)

    @PostMapping("skjerming")
    fun skjerming(@RequestBody brukerId: BrukerId) = skjerming.skjerming(brukerId)

    @PostMapping("skjerminger")
    fun skjerminger(@RequestBody ids: List<BrukerId>) = skjerming.skjerminger(ids)


}
