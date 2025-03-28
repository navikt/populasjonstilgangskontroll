package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import jakarta.persistence.*
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component

@Component
class NomEntityListener{
    private val log = getLogger(javaClass)

    @PrePersist
    private fun lagrer(entity : NomEntity) =
        log.trace("Lagrer ${entity.navid} i DB")

    @PreUpdate
    private fun oppdaterer(entity : NomEntity) =
        log.trace("Oppdaterer ${entity.navid} i DB")

    @PreRemove
    private fun fjerner(entity : NomEntity) =
        log.trace("Fjerner ${entity.navid} i DB")

    @PostPersist
    private fun lagret(entity : NomEntity) =
        log.trace("Lagret ${entity.navid} i DB")

    @PostUpdate
    private fun oppdatert(entity : NomEntity) =
        log.trace("Oppdaterte ${entity.navid} i DB")

    @PostRemove
    private fun fjernet(entity : NomEntity) =
        log.trace("Fjernet ${entity.navid} i DB")

    @PostLoad
    private fun lest(entity : NomEntity) =
        log.trace("Leste ${entity.navid} i DB")
}