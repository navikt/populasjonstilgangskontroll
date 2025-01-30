package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.RetryingOnRecoverable
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLConfig.Companion.PDL
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@Cacheable(PDL)
@RetryingOnRecoverable
class PersonTjeneste(private val adapter: PDLGraphQLClientAdapter) {
    fun hentPerson(fnr: Fødselsnummer) = adapter.person(fnr.verdi)
}