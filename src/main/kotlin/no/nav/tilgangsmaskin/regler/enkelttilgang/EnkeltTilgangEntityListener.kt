package no.nav.tilgangsmaskin.regler.enkelttilgang

import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import no.nav.tilgangsmaskin.tilgang.Token
import org.springframework.stereotype.Component

@Component
class EnkeltTilgangEntityListener(private val token: Token) {


    @PrePersist
    private fun lagrer(entity: EnkeltTilgangEntity) = setSource(entity)

    @PreUpdate
    private fun oppdaterer(entity: EnkeltTilgangEntity) = setSource(entity)


    fun setSource(target: EnkeltTilgangEntity) {
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
}