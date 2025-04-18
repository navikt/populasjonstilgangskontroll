package no.nav.tilgangsmaskin.ansatt

import org.springframework.core.env.Environment
import java.util.*


enum class GlobalGruppe(val property: String) {
    STRENGT_FORTROLIG("gruppe.strengt"),
    FORTROLIG("gruppe.fortrolig"),
    EGEN_ANSATT("gruppe.egenansatt"),
    UDEFINERT_GEO("gruppe.udefinert"),
    GEO_PERSON_UTLAND("gruppe.utland"),
    NASJONAL("gruppe.nasjonal");

    fun id(env: Environment) = UUID.fromString(env.getRequiredProperty(property))

}