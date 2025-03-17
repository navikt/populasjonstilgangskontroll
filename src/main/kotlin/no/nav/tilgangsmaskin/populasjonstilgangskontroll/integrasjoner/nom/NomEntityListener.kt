package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import jakarta.persistence.PostLoad
import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove
import jakarta.persistence.PostUpdate
import jakarta.persistence.PrePersist
import jakarta.persistence.PreRemove
import jakarta.persistence.PreUpdate
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenClaimsAccessor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NomEntityListener{

    @PrePersist
    private fun lagrer(entity : NomEntityListener) = log.trace("Lagrer {} i DB", entity.javaClass.simpleName)

    @PreUpdate
    private fun oppdaterer(entity : NomEntityListener) =  log.trace("Oppdaterer {} i DB", entity.javaClass.simpleName)

    @PreRemove
    private fun fjerner(entity : NomEntityListener) = log.trace("Fjerner {} fra DB", entity.javaClass.simpleName)

    @PostPersist
    private fun lagret(entity : NomEntityListener) = log.trace("Lagret {} i DB", entity.javaClass.simpleName)

    @PostUpdate
    private fun oppdatert(entity : NomEntityListener) = log.trace("Oppdaterte {} i DB", entity.javaClass.simpleName)

    @PostRemove
    private fun fjernet(entity : NomEntityListener) = log.trace("Fjernet {} fra DB", entity.javaClass.simpleName)

    @PostLoad
    private fun lest(entity : NomEntityListener) = log.trace("Leste {} fra DB", entity.javaClass.simpleName)

    companion object {
        private val log = LoggerFactory.getLogger(NomEntityListener::class.java)
    }
}