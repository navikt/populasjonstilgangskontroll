package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@Cacheable(SKJERMING)
class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {

    fun erSkjermet(ident: Fødselsnummer) = adapter.skjermetPerson(ident.verdi)
}