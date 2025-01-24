package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.personopplysninger.pdl

import org.springframework.stereotype.Service

@Service
class PDLService(private val adapter: PDLGraphQLClientAdapter) {
    fun hentPerson(fnr: Fødselsnummer) = adapter.person(fnr)
}