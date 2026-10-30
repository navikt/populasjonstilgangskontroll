package no.nav.tilgangsmaskin.regler.enkelttilgang

import no.nav.sikkerhetstjenesten.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.sikkerhetstjenesten.felles.utils.extensions.TimeExtensions.OSLO
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.Leder
import no.nav.tilgangsmaskin.bruker.BrukerId
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant
import java.time.Instant.now

@Repository
class EnkeltTilgangJPAAdapter(
    private val repo: EnkeltTilgangRepository,
    private val clock: Clock,
    private val entityListener: EnkeltTilgangEntityListener,
) {
    private val log = getLogger(javaClass)

    @Transactional
    fun enkeltTilgang(
        ansattId: String,
        enhetsnummer: String,
        data: EnkeltTilgangData,
        gt: String? = null,
        gtNavn: String? = null,
    ): EnkeltTilgangEntity {
        val expires =
            data.gyldigtil
                .plusDays(1)
                .atStartOfDay(OSLO)
                .minusMinutes(1)
                .toInstant()
        val nyEnkeltTilgang =
            EnkeltTilgangEntity(ansattId, data.brukerId.verdi, data.begrunnelse, enhetsnummer, expires, gt, gtNavn).apply {
                created = now(clock)
                updated = created
                entityListener.setSource(this)
            }
        val opprettet = repo.insertIfAbsent(nyEnkeltTilgang)
        val tilgang =
            checkNotNull(repo.findByNavidAndFnrAndExpires(ansattId, data.brukerId.verdi, expires)) {
                "Fant ikke enkelttilgang etter INSERT ON CONFLICT"
            }
        if (opprettet == 0) {
            log.info("Fant eksisterende enkelttilgang for navid=$ansattId, fnr=${data.brukerId.verdi.maskFnr()}, expires=$expires")
        }
        return tilgang
    }

    fun ikkeRapporterte(
        createdEtter: Instant,
        ekskluderteFnr: Set<String> = emptySet(),
    ): Set<EnkeltTilgang> =
        repo
            .findAllByRapportertIsNullAndCreatedAfterAndGtNavnIsNotNull(createdEtter)
            .filterNot { it.fnr in ekskluderteFnr }
            .mapTo(mutableSetOf()) { ansatt ->
                EnkeltTilgang(
                    AnsattId(ansatt.navid),
                    ansatt.begrunnelse,
                    ansatt.created,
                    ansatt.gt,
                    ansatt.system,
                    ansatt.gtNavn,
                    ansatt.enhet,
                    ansatt.expires,
                    requireNotNull(ansatt.id),
                )
            }

    fun markerRapportert(databaseIds: Set<Long>): Int = if (databaseIds.isEmpty()) 0 else repo.setRapportert(databaseIds, now(clock))

    fun gjeldendeTilgang(
        ansattId: String,
        brukerId: String,
        brukerIds: List<String>,
    ) = repo.gjeldende(ansattId, setOf(brukerId) + brukerIds, now(clock))

    fun gjeldendeTilganger(
        ansattId: String,
        brukerIds: Set<String>,
    ): Set<BrukerId> =
        repo
            .gjeldendeOverstyringer(ansattId, brukerIds, now(clock))
            .mapTo(mutableSetOf()) { BrukerId(it.fnr) }
}

data class EnkeltTilgang(
    val id: AnsattId,
    val begrunnelse: String,
    val created: Instant?,
    val gt: String?,
    val system: String?,
    val gtNavn: String?,
    val enhet: String?,
    val varighet: Instant,
    val databaseId: Long,
)

data class LederEnkeltTilganger(
    val leder: Leder,
    val ansatte: List<EnkeltTilgang>,
)
