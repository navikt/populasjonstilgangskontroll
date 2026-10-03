package no.nav.tilgangsmaskin.regler.enkelttilgang

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Tag
import io.micrometer.observation.annotation.Observed
import no.nav.sikkerhetstjenesten.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.sikkerhetstjenesten.felles.rest.ConsumerAwareHandlerInterceptor.Companion.USER_ID
import no.nav.sikkerhetstjenesten.felles.utils.extensions.TimeExtensions.diffFromNow
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.ansatt.entraproxy.EntraProxyTjeneste
import no.nav.tilgangsmaskin.ansatt.nom.Leder
import no.nav.tilgangsmaskin.ansatt.nom.NomTjeneste
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.BydelTilknytning
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.KommuneTilknytning
import no.nav.tilgangsmaskin.bruker.kodeverk.KodeverkTjeneste
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.withAnsattContext
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
    private val kodeverk: KodeverkTjeneste,
    private val kafka: EnkeltTilgangHendelseProdusent,
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
                val bruker = bruker.medNærmesteFamilie(data.brukerId.verdi)
                val gt = gt(bruker)
                val navn = gt?.let {
                      kodeverk.navn(it)
                }
                log.info("Enkelttilgang med gt $gt og navn ${navn ?: "ukjent"})")
                motor.kjerneregler(ansattTjeneste.ansatt(ansattId), bruker)
                MDC.put(USER_ID, ansattId.verdi)
                adapter.enkeltTilgang(ansattId.verdi, enhetsnummer, data, gt,navn)
                kafka.publiser(ansattId).also {
                    teller.tell(INGEN_REGEL_TAG, ENKELTTILGANG_GITT)
                    log.info("Enkelttilgang OK. $ansattId ved enhet $enhetsnummer har fått tilgang til ${data.brukerId} til og med ${data.gyldigtil}")
                }
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

    private fun gt(bruker: Bruker) =
        with(bruker.geografiskTilknytning) {
            when (this) {
                is KommuneTilknytning -> {
                    log.info("Enkelttilgang for ${bruker.oppslagId.maskFnr()} med gt ${kommune.verdi}")
                    kommune.verdi
                }
                is BydelTilknytning -> {
                    log.info("Enkelttilgang for ${bruker.oppslagId.maskFnr()} med gt ${bydel.verdi}")
                    bydel.verdi
                }
                else -> {
                    log.info("Enkelttilgang for ${bruker.oppslagId.maskFnr()} med annen geografisk tilknytning")
                    null
                }
            }
        }


    private fun gjeldendeEnkeltTilgang(ansattId: AnsattId, brukerId: BrukerId): Instant? =
        adapter.gjeldendeTilgang(ansattId.verdi, brukerId.verdi,
            bruker.medNærmesteFamilie(brukerId.verdi).historiskeIds.map {
                it.verdi
            })?.expires


    fun ikkeRapportertePrLeder(): List<LederEnkeltTilganger> {
        val ikkeRapporterte = adapter.ikkeRapporterte()
        val lederePerAnsatt = ikkeRapporterte
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

        val ansattePerLeder = mutableMapOf<Leder, MutableSet<EnkeltTilgang>>()
        ikkeRapporterte.forEach { tilgang ->
            val ledere = lederePerAnsatt.getValue(tilgang.id).ifEmpty { setOf(INGEN_LEDER) }
            ledere.forEach { leder ->
                ansattePerLeder.getOrPut(leder, ::mutableSetOf).add(tilgang)
            }
        }

        return ansattePerLeder
            .map { (leder, ansatte) ->
                LederEnkeltTilganger(
                    leder,
                    ansatte.sortedWith(compareBy({ it.id.verdi }, { it.created }, { it.begrunnelse })),
                )
            }
            .sortedWith(compareBy<LederEnkeltTilganger> { it.leder == INGEN_LEDER }
                .thenBy { it.leder.navident.verdi })
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