package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.AktørId
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.BrukerId
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
    override fun isValid(value: Any, context: ConstraintValidatorContext) =
        when (value) {
            is String -> runCatching { AktørId(value) }.isSuccess || runCatching { BrukerId(value) }.isSuccess
            is List<*> -> value.all { it is IdOgType && (runCatching { AktørId(it.brukerId) }.isSuccess || runCatching { BrukerId(it.brukerId) }.isSuccess) }
            else -> false
        }
}