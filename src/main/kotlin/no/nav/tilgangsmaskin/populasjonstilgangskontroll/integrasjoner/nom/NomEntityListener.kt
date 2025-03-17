package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import jakarta.persistence.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NomEntityListener{

    @PrePersist
    private fun lagrer(entity : NomEntity) = log.trace("Lagrer {} i DB", entity.javaClass.simpleName)

    @PreUpdate
    private fun oppdaterer(entity : NomEntity) =  log.trace("Oppdaterer {} i DB", entity.javaClass.simpleName)

    @PreRemove
    private fun fjerner(entity : NomEntity) = log.trace("Fjerner {} fra DB", entity.javaClass.simpleName)

    @PostPersist
    private fun lagret(entity : NomEntity) = log.trace("Lagret {} i DB", entity.javaClass.simpleName)

    @PostUpdate
    private fun oppdatert(entity : NomEntity) = log.trace("Oppdaterte {} i DB", entity.javaClass.simpleName)

    @PostRemove
    private fun fjernet(entity : NomEntity) = log.trace("Fjernet {} fra DB", entity.javaClass.simpleName)

    @PostLoad
    private fun lest(entity : NomEntity) = log.trace("Leste {} fra DB", entity.javaClass.simpleName)

    companion object {
        private val log = LoggerFactory.getLogger(NomEntity::class.java)
    }
}