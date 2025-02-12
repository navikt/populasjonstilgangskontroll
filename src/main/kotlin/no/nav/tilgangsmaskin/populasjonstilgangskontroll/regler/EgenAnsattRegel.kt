package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler

import  no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.GlobalGruppe.EGEN_GRUPPE
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(HIGHEST_PRECEDENCE + 2)
class EgenAnsattRegel(@Value("\${gruppe.egenansatt}") private val id: UUID) : AbstraktRegel(EGEN_GRUPPE, id, "42")

