package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.RetryingOnRecoverable
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming.SkjermingConfig.Companion.SKJERMING
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@Cacheable(SKJERMING)
@RetryingOnRecoverable
@ConditionalOnNotProd
class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {

    fun erSkjermet(ident: Fødselsnummer) = adapter.skjermetPerson(ident.verdi)
}