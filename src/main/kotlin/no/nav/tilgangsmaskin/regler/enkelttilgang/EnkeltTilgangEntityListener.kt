package no.nav.tilgangsmaskin.regler.enkelttilgang

import jakarta.persistence.PostLoad
import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove
import jakarta.persistence.PostUpdate
import jakarta.persistence.PrePersist
import jakarta.persistence.PreRemove
import jakarta.persistence.PreUpdate
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.maskFnr
import no.nav.tilgangsmaskin.tilgang.Token
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant

@Component
class EnkeltTilgangEntityListener(private val token: Token, private val clock: Clock) {

    private val log = getLogger(javaClass)

    @PrePersist
    private fun lagrer(entity: EnkeltTilgangEntity) = setCreated(entity).also {
        log.trace("Lagrer overstyring for ${entity.fnr.maskFnr()} i DB")
    }

    @PreUpdate
    private fun oppdaterer(entity: EnkeltTilgangEntity) = setOppdatert(entity).also {
        log.trace("Oppdaterer overstyring for ${entity.fnr.maskFnr()} i DB")
    }

    @PreRemove
    private fun fjerner(entity: EnkeltTilgangEntity) =
        log.trace("Fjerner overstyring for ${entity.fnr.maskFnr()} i DB")

    @PostPersist
    private fun lagret(entity: EnkeltTilgangEntity) =
        log.trace("Lagret overstyring for ${entity.fnr.maskFnr()} i DB")

    @PostUpdate
    private fun oppdatert(entity: EnkeltTilgangEntity) =
        log.trace("Oppdaterte overstyring for ${entity.fnr.maskFnr()} i DB")

    @PostRemove
    private fun fjernet(entity: EnkeltTilgangEntity) =
        log.trace("Fjernet overstyring for ${entity.fnr.maskFnr()} i DB")

    @PostLoad
    private fun lest(entity: EnkeltTilgangEntity) =
        log.trace("Leste overstyring for ${entity.fnr.maskFnr()} i DB")

    fun setCreatedBySystem(target: EnkeltTilgangEntity) {
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
    fun setOppdatert(target: EnkeltTilgangEntity) {
        setCreatedBySystem(target)
        val now = Instant.now(clock)
        target::class.java.declaredFields.forEach {
            if (it.isAnnotationPresent(LastModifiedDate::class.java)) {
                it.isAccessible = true
                it.set(target, now)
            }
        }
    }
    fun setCreated(target: EnkeltTilgangEntity) {
        setCreatedBySystem(target)
        val now = Instant.now(clock)
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