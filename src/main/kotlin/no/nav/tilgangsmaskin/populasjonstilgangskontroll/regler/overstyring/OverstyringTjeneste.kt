package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.Fødselsnummer
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.NavId
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class OverstyringTjeneste(private val adapter: JPAOverstyringAdapter) {

    private val log = getLogger(OverstyringTjeneste::class.java)

    fun harOverstyrtTilgang(id: NavId, fødselsnummer: Fødselsnummer) =
        adapter.findOverstyring(id.verdi, fødselsnummer.verdi) != null

    fun overstyr(ansattId: NavId, brukerId: Fødselsnummer) =
         adapter.lagre(ansattId.verdi, brukerId.verdi)
}

@Component
class JPAOverstyringAdapter(private val repository: OverstyringRepository)  {

    fun lagre(ansattId: String, brukerId: String) =
        repository.save(Overstyring().apply {
            navid = ansattId
            fnr = brukerId
        })

    fun findOverstyring(navid: String, fnr: String) = repository.findByNavidAndFnr(navid, fnr)
}