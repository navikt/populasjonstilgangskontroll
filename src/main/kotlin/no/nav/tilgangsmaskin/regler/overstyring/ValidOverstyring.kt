package no.nav.tilgangsmaskin.regler.overstyring

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [OverstyringValidator::class])
@Target(FIELD, VALUE_PARAMETER)
@Retention(RUNTIME)
annotation class ValidOverstyring(
    val message: String = "Overstyring må være fra nå og maks 3 måneder frem i tid",
    val groups: Array<KClass<*>> = [],
    val months: Long = 3,
    val minLengde: Int = 10,
    val maxLengde: Int = 255,
    val payload: Array<KClass<out Payload>> = [])