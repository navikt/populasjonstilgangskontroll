package no.nav.tilgangsmaskin.ansatt

import org.springframework.core.env.Environment
import java.util.*


val GLOBALE_GRUPPER = GlobalGruppe.entries //.map { it.property }.toSet()

enum class GlobalGruppe(val property: String) {
    STRENGT_FORTROLIG_GRUPPE("gruppe.strengt"),
    FORTROLIG_GRUPPE("gruppe.fortrolig"),
    EGEN_ANSATT_GRUPPE("gruppe.egenansatt"),
    UDEFINERT_GEO_GRUPPE("gruppe.udefinert"),
    GEO_PERSON_UTLAND_GRUPPE("gruppe.utland"),
    NASJONAL_GRUPPE("gruppe.nasjonal");

    fun id(env: Environment) = UUID.fromString(env.getRequiredProperty(property))

}