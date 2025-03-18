package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom


import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.AnsattId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.BrukerId
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NomHendelseKonsument(
    private val nom: NomTjeneste,
    annotationBeanConfigurerAspect: AnnotationBeanConfigurerAspect
) {

    private final val annotationBeanConfigurerAspect: AnnotationBeanConfigurerAspect = TODO("initialize me")
    private val log = getLogger(NomHendelseKonsument::class.java)
    @KafkaListener(topics = ["#{'\${nom.topic}'}"])
    fun listen(hendelse : NomHendelse) {
        with(hendelse) {
           log.info("Mottatt hendelse: {}", this)
           runCatching {
               validate(navident, personident)?.let {
                   nom.lagre(it.first, it.second, startdato,sluttdato)
               }
              }.onFailure {
                log.error("Feil ved lagring av hendelse: {} for $navident", it.message, it)
              }.getOrNull()?.also {
                log.info("Lagret hendelse med id: ${it.id} for $navident")
           }
        }
    }
    private fun validate(ansattId: String, brukerId: String) =
        runCatching {
            Pair(AnsattId(ansattId),BrukerId(brukerId))
        }.getOrNull().also {
            log.warn("($ansattId/$brukerId) kunne ikke validwres")
        }
}