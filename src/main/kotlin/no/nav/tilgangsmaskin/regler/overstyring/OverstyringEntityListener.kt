package no.nav.tilgangsmaskin.regler.overstyring

import jakarta.persistence.PostLoad
import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove
import jakarta.persistence.PostUpdate
import jakarta.persistence.PrePersist
import jakarta.persistence.PreRemove
import jakarta.persistence.PreUpdate
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.mask
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.stereotype.Component
import java.time.Instant.now

@Component
class OverstyringEntityListener(private val token: Token) {

    private val log = getLogger(javaClass)

    @PrePersist
    private fun lagrer(entity: OverstyringEntity) = setCrested(entity).also {
        log.trace("Lagrer overstyring for ${entity.fnr.mask()} i DB")
    }

    @PreUpdate
    private fun oppdaterer(entity: OverstyringEntity) = setOppdatert(entity).also {
        log.trace("Oppdaterer overstyring for ${entity.fnr.mask()} i DB")
    }

    @PreRemove
    private fun fjerner(entity: OverstyringEntity) =
        log.trace("Fjerner overstyring for ${entity.fnr.mask()} i DB")

    @PostPersist
    private fun lagret(entity: OverstyringEntity) =
        log.trace("Lagret overstyring for ${entity.fnr.mask()} i DB")

    @PostUpdate
    private fun oppdatert(entity: OverstyringEntity) =
        log.trace("Oppdaterte overstyring for ${entity.fnr.mask()} i DB")

    @PostRemove
    private fun fjernet(entity: OverstyringEntity) =
        log.trace("Fjernet overstyring for ${entity.fnr.mask()} i DB")

    @PostLoad
    private fun lest(entity: OverstyringEntity) =
        log.trace("Leste overstyring for ${entity.fnr.mask()} i DB")

    fun setCreatedBySystem(target: OverstyringEntity) {
        target::class.java.declaredFields.forEach {
            if (it.isAnnotationPresent(CreatedBySystem::class.java)) {
                it.isAccessible = true
                it.set(target, token.system)
            }
            if (it.isAnnotationPresent(CreatedByAnsatt::class.java)) {
                it.isAccessible = true
                it.set(target, token.ansattId?.verdi ?: token.system)
            }
        }
    }
    fun setOppdatert(target: OverstyringEntity) {
        setCreatedBySystem(target)
        target::class.java.declaredFields.forEach {
            if (it.isAnnotationPresent(LastModifiedDate::class.java)) {
                it.isAccessible = true
                it.set(target, now())
            }
        }
    }
    fun setCrested(target: OverstyringEntity) {
        setCreatedBySystem(target)
        val now = now()
        target::class.java.declaredFields.forEach {
            if (it.isAnnotationPresent(LastModifiedDato::class.java)) {
                it.isAccessible = true
                it.set(target, now)
            }
            if (it.isAnnotationPresent(CreatedDato::class.java)) {
                it.isAccessible = true
                it.set(target, now)
            }
        }
    }
}