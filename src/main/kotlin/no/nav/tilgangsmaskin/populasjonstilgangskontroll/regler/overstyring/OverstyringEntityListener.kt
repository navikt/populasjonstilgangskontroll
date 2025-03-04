package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import jakarta.persistence.PostLoad
import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove
import jakarta.persistence.PostUpdate
import jakarta.persistence.PrePersist
import jakarta.persistence.PreRemove
import jakarta.persistence.PreUpdate
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenAccessor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OverstyringEntityListener(private val token: TokenAccessor) {

    @PrePersist
    private fun lagrer(entity : OverstyringEntity) =  setCreatedBySystem(entity).also {
        log.trace("Lagrer {} i DB", entity.javaClass.simpleName)
    }

    @PreUpdate
    private fun oppdaterer(entity : OverstyringEntity) = setCreatedBySystem(entity).also {
        log.trace("Oppdaterer {} i DB", entity.javaClass.simpleName)
    }

    @PreRemove
    private fun fjerner(entity : OverstyringEntity) = log.trace("Fjerner {} fra DB", entity.javaClass.simpleName)

    @PostPersist
    private fun lagret(entity : OverstyringEntity) = log.trace("Lagret {} i DB", entity.javaClass.simpleName)

    @PostUpdate
    private fun oppdatert(entity : OverstyringEntity) = log.trace("Oppdaterte {} i DB", entity.javaClass.simpleName)

    @PostRemove
    private fun fjernet(entity : OverstyringEntity) = log.trace("Fjernet {} fra DB", entity.javaClass.simpleName)

    @PostLoad
    private fun lest(entity : OverstyringEntity) = log.trace("Leste {} fra DB", entity.javaClass.simpleName)

    companion object {

        private val log = LoggerFactory.getLogger(OverstyringEntityListener::class.java)
    }
    fun setCreatedBySystem(target: Any) {
        target::class.java.declaredFields.forEach { field ->
            if (field.isAnnotationPresent(CreatedBySystem::class.java)) {
                field.isAccessible = true
                field.set(target, token.system)
            }
        }
    }
}