package no.nav.tilgangsmaskin.tilgang

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.BrukerOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.BulkResultat
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.regler.motor.AggregertBulkRespons
import no.nav.tilgangsmaskin.regler.motor.AggregertBulkRespons.EnkeltBulkRespons
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class BulkResponsAggregator(
    private val enkeltTilgangTjeneste: EnkeltTilgangTjeneste,
    private val auditor: Auditor
) {
    private val log = getLogger(javaClass)

    fun aggreger(ansattId: AnsattId, ansatt: Ansatt, resultater: Set<BulkResultat>, oppgitt: Set<BrukerIdOgRegelsett>, brukere: Set<BrukerOgRegelsett>): AggregertBulkRespons {
            log.debug("${resultater.size} bulk resultater {}", resultater.map { resultat -> "${resultat.bruker.oppslagId.maskFnr()}: ${resultat.status}" })
        val godkjente = godkjente(ansatt, resultater)
        val avviste = avviste(ansatt, godkjente, resultater, brukere)
        val ikkeFunnet = ikkeFunnet(oppgitt, resultater)
        return AggregertBulkRespons(ansattId, godkjente + avviste + ikkeFunnet)
    }

    private fun godkjente(ansatt: Ansatt, resultater: Set<BulkResultat>) =
        buildSet {
            val (godkjente, avviste) = resultater.partition { it.status.is2xxSuccessful }
            godkjente.forEach { add(EnkeltBulkRespons.ok(it.bruker.oppslagId)) }
            enkeltTilgangTjeneste
                .tilganger(ansatt.ansattId, avviste.map { it.bruker.brukerId }.toSet())
                .forEach { add(EnkeltBulkRespons.ok(it.verdi)) }
        }.also { respons ->
            if (respons.isNotEmpty()) {
                log.debug("${respons.size} godkjent av bulk ({})", respons.map { it.brukerId.maskFnr() })
            }
        }

    private fun avviste(ansatt: Ansatt, godkjente: Set<EnkeltBulkRespons>, resultater: Set<BulkResultat>, brukere: Set<BrukerOgRegelsett>) =
        buildSet {
            val godkjenteIds = godkjente.map { it.brukerId }.toSet()
            for (resultat in resultater) {
                log.trace("Bulk sjekker om avvist ident {} har enkelttilgang", resultat.bruker.oppslagId.maskFnr())
                if (resultat.status == HttpStatus.FORBIDDEN && resultat.bruker.oppslagId !in godkjenteIds) {
                    log.trace("Bulk avvist ident {} har ingen enkelttilgang", resultat.bruker.oppslagId.maskFnr())
                    add(EnkeltBulkRespons(RegelException(ansatt,
                        brukere.finnBruker(resultat.bruker.oppslagId),
                        resultat.regel!!,
                        status = resultat.status)))
                }
            }
        }.also {
            if (it.isNotEmpty()) {
                log.debug("${it.size} avvist av bulk ({})", it.map { ident -> ident.brukerId.maskFnr() })
            }
        }

    private fun ikkeFunnet(oppgitt: Set<BrukerIdOgRegelsett>, funnet: Set<BulkResultat>) =
        buildSet {
            for (item in (oppgitt - funnet)) {
                add(EnkeltBulkRespons.ok(item.brukerId))
            }
        }.also {
            if (it.isNotEmpty()) {
                auditor.info("${it.size} brukere med identer ${it.map { ident -> ident.brukerId}} ikke funnet i PDL ved oppslag")
                log.debug("${it.size} ikke funnet i bulk ({})", it.map { ident -> ident.brukerId.maskFnr() })
            }
        }

    private operator fun Set<BrukerIdOgRegelsett>.minus(funnet: Set<BulkResultat>) = filterNot { brukerIdOgRegelsett ->
        brukerIdOgRegelsett.brukerId in (funnet.flatMap { it.bruker.historiskeIds.map { id -> id.verdi } } + funnet.map { it.bruker.oppslagId })
    }

    private fun Set<BrukerOgRegelsett>.finnBruker(oppslagId: String) =
        first { it.bruker.oppslagId == oppslagId }.bruker
}