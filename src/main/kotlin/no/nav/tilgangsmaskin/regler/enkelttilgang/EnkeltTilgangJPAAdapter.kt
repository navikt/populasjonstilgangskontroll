package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId.systemDefault

@Component
class EnkeltTilgangJPAAdapter(private val repo: EnkeltTilgangRepository) {

    fun enkeltTilgang(ansattId: String, enhetsnummer: String, data: EnkeltTilgangData) =
        with(data) {
            repo.save(EnkeltTilgangEntity(ansattId, brukerId.verdi, begrunnelse, enhetsnummer,gyldigtil.atStartOfDay(systemDefault()).toInstant()))
            Unit
        }

    @Transactional(readOnly = true)
    fun gjeldendeOverstyring(ansattId: String, brukerId: String, brukerIds: List<String>) =
        repo.gjeldendeOverstyring(ansattId, setOf(brukerId) + brukerIds)

    @Transactional(readOnly = true)
    fun gjeldendeTilganger(ansattId: String, brukerIds: Set<String>): Set<BrukerId> =
        repo.gjeldendeOverstyringer(ansattId, brukerIds)
            .mapTo(mutableSetOf()) { BrukerId(it.fnr) }
}