package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant.now
import java.time.ZoneId.systemDefault

@Component
class EnkeltTilgangJPAAdapter(
    private val repo: EnkeltTilgangRepository,
    private val clock: Clock,
) {

    fun enkeltTilgang(ansattId: String, enhetsnummer: String, data: EnkeltTilgangData) =
        with(data) {
            repo.save(EnkeltTilgangEntity(ansattId, brukerId.verdi, begrunnelse, enhetsnummer,gyldigtil.atStartOfDay(systemDefault()).toInstant()))
        }

    fun gjeldende(ansattId: String, brukerId: String, brukerIds: List<String>) =
        repo.gjeldende(ansattId, setOf(brukerId) + brukerIds, cutoff())

    fun gjeldendeTilganger(ansattId: String, brukerIds: Set<String>): Set<BrukerId> =
        repo.gjeldendeOverstyringer(ansattId, brukerIds, cutoff())
            .mapTo(mutableSetOf()) { BrukerId(it.fnr) }

    private fun cutoff() = now(clock)
}