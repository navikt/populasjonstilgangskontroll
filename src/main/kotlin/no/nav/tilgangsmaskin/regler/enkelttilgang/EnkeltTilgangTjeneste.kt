package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Tag
import io.micrometer.observation.annotation.Observed
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.Leder
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.withAnsattContext
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.diffFromNow
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.motor.RegelMotorLogger.Companion.INGEN_REGEL_TAG
import no.nav.tilgangsmaskin.regler.motor.RegelMotorLogger.Companion.tag
import org.jboss.logging.MDC
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant


@Observed
@Service
@Transactional(readOnly = true)
@Timed
class EnkeltTilgangTjeneste(
    private val ansattTjeneste: AnsattTjeneste,
    private val nom: NomTjeneste,
    private val bruker: BrukerTjeneste,
    private val adapter: EnkeltTilgangJPAAdapter,
    private val motor: RegelMotor,
    private val proxy: EntraProxyTjeneste,
    private val clock: Clock,
    private val teller: EnkeltTilgangTeller) {

    private val log = getLogger(javaClass)

    fun tilganger(ansattId: AnsattId, brukerIds: Set<BrukerId>) =
        adapter.gjeldendeTilganger(ansattId.verdi, brukerIds.map { it.verdi }.toSet())

    fun harTilgang(ansattId: AnsattId, brukerId: BrukerId) =
        gjeldendeEnkeltTilgang(ansattId, brukerId)
            ?.also {
                log.trace("Enkelttilgang er gyldig i {} til for {} og {}", it.diffFromNow(clock), ansattId, brukerId)
            } != null


    @Transactional
    fun registrerTilgang(ansattId: AnsattId, data: EnkeltTilgangData): Boolean =
        withAnsattContext(ansattId)  {
            runCatching {
                val enhetsnummer = enhetsNummerFor(ansattId)
                motor.kjerneregler(ansattTjeneste.ansatt(ansattId),
                    bruker.medNærmesteFamilie(data.brukerId.verdi))
                MDC.put(USER_ID, ansattId.verdi)
                adapter.enkeltTilgang(ansattId.verdi, enhetsnummer, data)
                teller.tell(INGEN_REGEL_TAG, ENKELTTILGANG_GITT)
                log.info("Enkelttilgang OK. $ansattId ved enhet $enhetsnummer har fått tilgang til ${data.brukerId} til og med ${data.gyldigtil}")
                true
            }.onFailure { e ->
                when (e) {
                    is RegelException -> {
                        log.warn("Enkelttilgang er avvist av kjerneregler for $ansattId og ${data.brukerId}", e)
                        teller.tell(e.regel.tag(), ENKELTTILGANG_AVVIST)
                    }
                }
            }.getOrThrow()
        }



    private fun gjeldendeEnkeltTilgang(ansattId: AnsattId, brukerId: BrukerId): Instant? =
        adapter.gjeldendeTilgang(ansattId.verdi, brukerId.verdi,
            bruker.medNærmesteFamilie(brukerId.verdi).historiskeIds.map {
                it.verdi
            })?.expires


    fun ikkeRapportertePrLeder(): List<LederEnkeltTilganger> {
        val ikkeRapporterte = adapter.ikkeRapporterte()
        val ansattePrLeder = ikkeRapporterte
            .map { it.id }
            .distinct()
            .associateWith { ansattId ->
                nom.lederForAnsatt(ansattId)
                    ?.orgTilknytninger
                    .orEmpty()
                    .filter { it.erDagligOppfolging }
                    .flatMapTo(mutableSetOf()) { it.orgEnhet.ledere }
                    .mapTo(sortedSetOf(compareBy { it.navident.verdi })) { it.ressurs }
            }

        val ansattePerLeder: Map<Leder, Set<EnkeltTilgang>> = ikkeRapporterte
            .flatMap { tilgang ->
                val ledere = ansattePrLeder.getValue(tilgang.id)
                val ledereEllerUtenLeder =
                    if (ledere.isEmpty()) setOf(INGEN_LEDER) else ledere
                ledereEllerUtenLeder.map { leder -> leder to tilgang }
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, ansatte) -> ansatte.toSet() }

        return ansattePerLeder
            .map { (leder, ansatte) ->
                LederEnkeltTilganger(
                    leder,
                    ansatte.sortedWith(compareBy({ it.id.verdi }, { it.created }, { it.begrunnelse })),
                )
            }
            .sortedBy { it.leder.navident.verdi }
    }

    private fun enhetsNummerFor(ansattId: AnsattId) =
        runCatching {
            proxy.enhet(ansattId).enhetnummer.verdi
        }.getOrDefault(UTILGJENGELIG)


    private companion object {
        private const val TAG = "overstyrt"
        private val INGEN_LEDER = Leder("ingen@nav.no", AnsattId("A000000"), "Ingen leder")
        private val ENKELTTILGANG_GITT = Tag.of(TAG, "true")
        private val ENKELTTILGANG_AVVIST = Tag.of(TAG, "false")
    }
}