package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import jakarta.persistence.PostLoad
import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove
import jakarta.persistence.PostUpdate
import jakarta.persistence.PrePersist
import jakarta.persistence.PreRemove
import jakarta.persistence.PreUpdate
import org.slf4j.LoggerFactory

class LoggingEntityListener {

    @PrePersist
    private fun lagrer(entity : Any) = log.trace("Lagrer {} i DB", entity)

    @PreUpdate
    private fun oppdaterer(entity : Any) = log.trace("Oppdaterer {} i DB", entity)

    @PreRemove
    private fun fjerner(entity : Any) = log.trace("Fjerner {} fra DB", entity)

    @PostPersist
    private fun lagret(entity : Any) = log.trace("Lagret {} i DB", entity)

    @PostUpdate
    private fun oppdatert(entity : Any) = log.trace("Oppdaterte {} i DB", entity)

    @PostRemove
    private fun fjernet(entity : Any) = log.trace("Fjernet {} fra DB", entity)

    @PostLoad
    private fun lest(entity : Any) = log.trace("Leste {} fra DB", entity)

    companion object {

        private val log = LoggerFactory.getLogger(LoggingEntityListener::class.java)
    }
}