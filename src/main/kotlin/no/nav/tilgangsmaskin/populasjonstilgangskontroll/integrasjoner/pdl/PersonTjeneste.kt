package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.RetryingOnRecoverable
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.PDLConfig.Companion.PDL
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.KandidatMapper
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@Cacheable(PDL)
@RetryingOnRecoverable
@ConditionalOnNotProd
class PersonTjeneste(private val adapter: PDLGraphQLClientAdapter) {
    fun kandidat(fnr: Fødselsnummer) = adapter.person(fnr.verdi).let { KandidatMapper.mapToKandidat(fnr,it,adapter.gt(fnr.verdi),false) }
    fun gt(fnr: Fødselsnummer) = adapter.gt(fnr.verdi)
}