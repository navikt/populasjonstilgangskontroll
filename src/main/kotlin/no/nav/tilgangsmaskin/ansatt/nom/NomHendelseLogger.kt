package no.nav.tilgangsmaskin.ansatt.nom


import io.micrometer.core.instrument.MeterRegistry
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.pluralize
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class NomHendelseLogger(private val registry: MeterRegistry, private val repo: NomRepository) {

    init {
        size()
    }
    private val log = getLogger(javaClass)
    fun ok(ansattId: String, brukerId: String) {
        log.info("Lagret brukerId ${brukerId.maskFnr()} for $ansattId OK")
    }

    fun start(hendelser: List<NomHendelse>) {
        log.info("Mottok ${"hendelse".pluralize(hendelser)}")
    }

    fun behandler(hendelse: NomHendelse) {
        log.info("Behandler hendelse: {}", hendelse)
    }

    fun ferdig(hendelser: List<NomHendelse>) {
        log.info("${"hendelse".pluralize(hendelser)} ferdig behandlet").also {
            size()
        }
    }

    private fun size() =
        registry.gauge("nom.size",repo) {
            repo.count().toDouble()
        }
    fun feilet(ansattId: String, brukerId: String, e: Throwable) {
        log.error("Kunne ikke lagre brukerId ${brukerId.maskFnr()} for $ansattId (${e.message})", e)
    }
}