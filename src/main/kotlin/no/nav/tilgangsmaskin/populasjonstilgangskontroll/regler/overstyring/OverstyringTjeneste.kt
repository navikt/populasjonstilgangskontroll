package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import org.springframework.stereotype.Component

@Component
class OverstyringTjeneste(private val adapter: JPAOverstyringAdapter) {
    fun harOverstyrtTilgang(id: NavId, fødselsnummer: Fødselsnummer) : Boolean {
        return false  // TODO
    }

    fun overstyr(ansattId: NavId, brukerId: Fødselsnummer) {
         adapter.lagre(ansattId.verdi, brukerId.verdi)
    }
}

@Component
class JPAOverstyringAdapter(private val repository: OverstyringRepository)  {

    fun lagre(ansattId: String, brukerId: String) {
        repository.save(Overstyring().apply {
            navid = ansattId
            fnr = brukerId
        })
    }
}