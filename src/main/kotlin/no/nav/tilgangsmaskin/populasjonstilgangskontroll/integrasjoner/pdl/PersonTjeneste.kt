package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import org.springframework.stereotype.Service

@Service
class PersonTjeneste(private val adapter: PDLGraphQLClientAdapter) {
    fun hentPerson(fnr: Fødselsnummer) = adapter.person(fnr.verdi)
}