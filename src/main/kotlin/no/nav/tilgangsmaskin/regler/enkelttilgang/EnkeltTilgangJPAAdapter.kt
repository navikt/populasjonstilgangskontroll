package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.tilgangsmaskin.bruker.BrukerId
import org.springframework.stereotype.Repository
import org.springframework.dao.DataIntegrityViolationException
import java.time.Clock
import java.time.Instant.now

@Repository
class EnkeltTilgangJPAAdapter(
    private val repo: EnkeltTilgangRepository,
    private val clock: Clock) {

    fun enkeltTilgang(ansattId: String, enhetsnummer: String, data: EnkeltTilgangData): EnkeltTilgangEntity {
        val expires = data.gyldigtil.plusDays(1)
            .atStartOfDay(clock.zone)
            .toInstant()
        repo.findByNavidAndFnrAndExpires(ansattId, data.brukerId.verdi, expires)?.let { return it }

        val nyEnkeltTilgang = EnkeltTilgangEntity(ansattId, data.brukerId.verdi, data.begrunnelse, enhetsnummer, expires)

        return try {
            repo.saveAndFlush(nyEnkeltTilgang)
        } catch (e: DataIntegrityViolationException) {
            repo.findByNavidAndFnrAndExpires(ansattId, data.brukerId.verdi, expires) ?: throw e
        }
    }

    fun gjeldendeTilgang(ansattId: String, brukerId: String, brukerIds: List<String>) =
        repo.gjeldende(ansattId, setOf(brukerId) + brukerIds, cutoff())

    fun gjeldendeTilganger(ansattId: String, brukerIds: Set<String>): Set<BrukerId> =
        repo.gjeldendeOverstyringer(ansattId, brukerIds, cutoff())
            .mapTo(mutableSetOf()) { BrukerId(it.fnr) }

    private fun cutoff() = now(clock)
}