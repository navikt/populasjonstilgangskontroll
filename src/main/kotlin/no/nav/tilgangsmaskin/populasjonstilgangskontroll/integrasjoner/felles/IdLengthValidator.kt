package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.regler.IdOgType
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [IdValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidId(
    val message: String = "Invalid ID or list of IDs",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class IdValidator : ConstraintValidator<ValidId, Any> {
    override fun isValid(value: Any, context: ConstraintValidatorContext): Boolean {
        context.disableDefaultConstraintViolation()
        if (value is String) {
            if (value.length == 11 || value.length == 13) {
                return true
            }
            context.buildConstraintViolationWithTemplate("String length must be 11 or 13").addConstraintViolation()
            return false
        }

        if (value is List<*>) {
            if (value.all { it is IdOgType && (it.brukerId.length == 11 || it.brukerId.length == 13) }) {
                return true
            }
            context.buildConstraintViolationWithTemplate("All entries in the list must have a brukerId length of 11 or 13").addConstraintViolation()
            return false
        }
        return false
    }
}