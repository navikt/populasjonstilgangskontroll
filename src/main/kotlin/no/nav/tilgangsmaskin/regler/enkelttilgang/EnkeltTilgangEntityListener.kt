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


    private fun setSource(target: EnkeltTilgangEntity) {
        target.system = token.system
        target.oppretter = token.ansattId?.verdi ?: token.system
    }
}