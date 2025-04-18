package no.nav.tilgangsmaskin.ansatt

import no.nav.tilgangsmaskin.regler.motor.BeskrivelseTekster
import org.springframework.core.env.Environment
import java.util.*


enum class GlobalGruppe(val property: String, val metadata: BeskrivelseTekster) {
    STRENGT_FORTROLIG("gruppe.strengt", BeskrivelseTekster.STRENGT_FORTROLIG_ADRESSE),
    FORTROLIG("gruppe.fortrolig", BeskrivelseTekster.FORTROLIG_ADRESSE),
    EGEN_ANSATT("gruppe.egenansatt", BeskrivelseTekster.EGNEDATA),
    UKJENT_BOSTED("gruppe.udefinert", BeskrivelseTekster.PERSON_UKJENT),
    BOSTED_UTLAND("gruppe.utland", BeskrivelseTekster.PERSON_UTLAND),
    NASJONAL("gruppe.nasjonal", BeskrivelseTekster.GEOGRAFISK);


    fun id(env: Environment) = UUID.fromString(env.getRequiredProperty(property))

}