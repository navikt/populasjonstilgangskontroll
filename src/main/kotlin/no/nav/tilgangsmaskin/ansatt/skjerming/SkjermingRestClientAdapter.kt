package no.nav.tilgangsmaskin.ansatt.skjerming

import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENT
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.IDENTER
import no.nav.tilgangsmaskin.ansatt.skjerming.SkjermingConfig.Companion.SKJERMING
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.AbstractRestClientAdapter
import no.nav.tilgangsmaskin.felles.rest.cache.ValKeyAdapter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class SkjermingRestClientAdapter(@Qualifier(SKJERMING) restClient: RestClient, private val cf: SkjermingConfig, private val valkey: ValKeyAdapter) : AbstractRestClientAdapter(restClient, cf) {

    fun skjerming(ident: String) = post<Boolean>(cf.skjermingUri, mapOf(IDENT to ident))

    fun skjerminger(identer: Set<String>): Map<BrukerId, Boolean> {
        if (identer.isEmpty()) return emptyMap()
        else {
            val cached  = skjermingerFraCache(identer).also {
                if (it.size < identer.size) {
                    log.info("Ikke alle ${identer.size} skjerminger ble funnet i cache, det mangler ${identer.size - it.size}")
                }
                else {
                    log.info("Alle (${identer.size}) skjerminger ble funnet i cache, returnerer")
                    return it
                }
            }
            val gjenværende = identer.minus(cached.keys.map { it.verdi })
            val slåttOpp = skjermingerFraREST(gjenværende)
            log.info("Slo opp ${slåttOpp.size} gjenværende skjerminger fra REST for ${identer.size} identer, ${cached.size} fra cache")

            valkey.mset(SKJERMING, *slåttOpp.map { it.key.verdi to it.value }.toList()
                .toTypedArray<Pair<String, Boolean>>()
            )
            val alle = skjermingerFraCache(identer).also {
                if (it.size == identer.size) {
                    log.info("Hentet som forventet ${it.size} skjerminger fra cache etter oppdatering av cache")
                }
            }  // Skal nå treffe alle
            return alle
            //return slåttOpp
        }
    }

    private fun skjermingerFraREST(identer: Set<String>): Map<BrukerId, Boolean> =
        post<Map<String, Boolean>>(cf.skjermingerUri, mapOf(IDENTER to identer))
            .map { (brukerId, skjerming) -> BrukerId(brukerId) to skjerming }
            .toMap()

    private fun skjermingerFraCache(identer: Set<String>) =
        runCatching {
            valkey.skjerminger(*identer.toTypedArray())
                .associate { (ident, skjerming) -> BrukerId(ident) to skjerming }
        }.getOrElse {
            log.warn("Kunne ikke hente skjerminger for $identer identer fra cache",it)
            emptyMap()
        }
}


