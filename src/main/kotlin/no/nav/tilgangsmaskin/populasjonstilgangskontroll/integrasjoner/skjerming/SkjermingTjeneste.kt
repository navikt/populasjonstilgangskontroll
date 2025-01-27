package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl.Fødselsnummer
import org.springframework.stereotype.Service

@Service
class SkjermingTjeneste(private val adapter: SkjermingRestClientAdapter) {

    fun erSkjermet(ident: Fødselsnummer)= adapter.skjermetPerson(ident.verdi)
}