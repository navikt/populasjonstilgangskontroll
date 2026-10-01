package no.nav.tilgangsmaskin.ansatt.kodeverk

import io.micrometer.observation.annotation.Observed
import no.nav.tilgangsmaskin.ansatt.kodeverk.KodeverkConfig.Companion.KODEVERK
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.rest.RestRetryingWhenRecoverableService
import org.springframework.web.service.registry.ImportHttpServices

@Observed
@RestRetryingWhenRecoverableService
@ImportHttpServices(types = [KodeverkClient::class], group = KODEVERK)
class KodeverkTjeneste(private val client: KodeverkClient) {

    private fun koderOgNavn() =
        client.bydeler().kodeOgNavn() + client.kommuner().kodeOgNavn()

    fun navn(kode: String) =
        koderOgNavn().singleOrNull { it.kode == kode }?.tekst

    @NoCoverageAnalysis
    override fun toString() = "${javaClass.simpleName} [client=$client]"
}

