package no.nav.tilgangsmaskin.felles

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.IdOgType
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [IdValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidId(
    val message: String = "Ugyldig ID eller liste av IDs",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class IdValidator : ConstraintValidator<ValidId, Any> {
    override fun isValid(verdi: Any, context: ConstraintValidatorContext) =
        when (verdi) {
            is String -> runCatching { AktørId(verdi) }.isSuccess || runCatching { BrukerId(verdi) }.isSuccess
            is List<*> -> verdi.all {
                it is IdOgType && (runCatching { AktørId(it.brukerId) }.isSuccess || runCatching {
                    BrukerId(
                        it.brukerId
                    )
                }.isSuccess)
            }

            else -> false
        }
}