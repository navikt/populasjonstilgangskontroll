package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.stereotype.Component
import java.time.ZoneId.systemDefault

@Component
class EnkeltTilgangJPAAdapter(private val repository: EnkeltTilgangRepository) {

    fun overstyr(ansattId: String, enhetsnummer: String, data: EnkeltTilgangData) =
        with(data) {
            repository.save(EnkeltTilgangEntity(ansattId, brukerId.verdi, begrunnelse, enhetsnummer,gyldigtil.atStartOfDay(systemDefault()).toInstant()))
            Unit
        }

    fun gjeldendeOverstyring(ansattId: String, brukerId: String, brukerIds: List<String>) =
        repository.gjeldendeOverstyring(ansattId, listOf(brukerId) + brukerIds)

    fun gjeldendeTilganger(ansattId: String, brukerIds: List<String>): List<BrukerId> =
        repository.gjeldendeOverstyringer(ansattId, brukerIds)
            .map { BrukerId(it.fnr) }
}