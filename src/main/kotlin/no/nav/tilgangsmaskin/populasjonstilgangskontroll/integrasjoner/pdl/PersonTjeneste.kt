package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.RetryableOnRecoverableException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLConfig.Companion.PDL
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@Cacheable(PDL)
class PersonTjeneste(private val adapter: PDLGraphQLClientAdapter) {
@RetryableOnRecoverableException
    fun hentPerson(fnr: Fødselsnummer) = adapter.person(fnr.verdi)
}