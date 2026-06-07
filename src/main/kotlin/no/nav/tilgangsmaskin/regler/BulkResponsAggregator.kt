package no.nav.tilgangsmaskin.regler

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.felles.utils.Auditor
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.regler.enkelttilgang.EnkeltTilgangTjeneste
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.BrukerOgRegelsett
import no.nav.tilgangsmaskin.regler.motor.BulkResultat
import no.nav.tilgangsmaskin.regler.motor.RegelException
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons
import no.nav.tilgangsmaskin.tilgang.AggregertBulkRespons.EnkeltBulkRespons
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class BulkResponsAggregator(
    private val enkeltTilgangTjeneste: EnkeltTilgangTjeneste,
    private val auditor: Auditor) {

    private val log = getLogger(javaClass)

    fun aggreger(ansattId: AnsattId, ansatt: Ansatt, oppgitt: Set<BrukerIdOgRegelsett>, resultater: Set<BulkResultat>, brukere: Set<BrukerOgRegelsett>): AggregertBulkRespons {
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
                log.debug("Bulk godkjente oppslagId(s) {}", respons.map { it.brukerId.maskFnr() })
            }
        }

    private fun avviste(ansatt: Ansatt, godkjente: Set<EnkeltBulkRespons>, resultater: Set<BulkResultat>, brukere: Set<BrukerOgRegelsett>) =
        buildSet {
            val godkjenteIds = godkjente.mapTo(mutableSetOf()) { it.brukerId }
            for (resultat in resultater) {
                if (resultat.status == HttpStatus.FORBIDDEN && resultat.bruker.oppslagId !in godkjenteIds) {
                    log.trace("Bulk resultat for {} har ingen enkelttilgang", resultat.bruker.oppslagId.maskFnr())
                    add(EnkeltBulkRespons(RegelException(ansatt,
                        brukere.finnBruker(resultat.bruker.oppslagId),
                        resultat.regel!!,
                        status = resultat.status)))
                }
            }
        }.also {
            if (it.isNotEmpty()) {
                log.debug("Bulk avviste {}", it)
            }
        }

    private fun ikkeFunnet(oppgitt: Set<BrukerIdOgRegelsett>, funnet: Set<BulkResultat>) =
        buildSet {
            for (item in (oppgitt - funnet)) {
                add(EnkeltBulkRespons.ok(item.brukerId))
            }
        }.also {
            if (it.isNotEmpty()) {
                auditor.info("404: Brukere med identer ${it.map { ident -> ident.brukerId.maskFnr() }} ikke funnet i PDL ved oppslag")
                log.debug("${it.size} bulk elementer ikke funnet")
            }
        }

    private operator fun Set<BrukerIdOgRegelsett>.minus(funnet: Set<BulkResultat>) = filterNot { brukerIdOgRegelsett ->
        brukerIdOgRegelsett.brukerId in (funnet.flatMap { it.bruker.historiskeIds.map { id -> id.verdi } } + funnet.map { it.bruker.oppslagId })
    }

    private fun Set<BrukerOgRegelsett>.finnBruker(oppslagId: String) =
        first { it.bruker.oppslagId == oppslagId }.bruker
}

