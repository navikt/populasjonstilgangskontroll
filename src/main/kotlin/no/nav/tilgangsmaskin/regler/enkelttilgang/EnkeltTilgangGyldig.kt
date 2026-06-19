package no.nav.tilgangsmaskin.regler.enkelttilgang

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [EnkeltTilgangValidator::class])
@Target(FIELD, VALUE_PARAMETER)
@Retention(RUNTIME)
annotation class EnkeltTilgangGyldig(
    val message: String = "Enkelttilgang må være fra nå og maks 3 måneder frem i tid, og begrunnelse må være mellom 10 og 255 tegn",
    val groups: Array<KClass<*>> = [],
    val months: Long = 3,
    val min: Int = 10,
    val max: Int = 255,
    val payload: Array<KClass<out Payload>> = [])