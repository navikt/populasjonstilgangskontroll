package no.nav.tilgangsmaskin.felles.rest

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.motor.IdOgType
import org.slf4j.LoggerFactory.getLogger
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [IdValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidId(
        val message: String = "Ugyldig ID eller liste av IDs",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = [])

class IdValidator : ConstraintValidator<ValidId, Any> {
    private val log = getLogger(javaClass)
    override fun isValid(verdi: Any, context: ConstraintValidatorContext) =
        when (verdi) {
            is String -> runCatching { AktørId(verdi.trim('"')) }.isSuccess || runCatching { BrukerId(verdi.trim('"')) }.isSuccess
            is Set<*> -> verdi.all {
                log.info("XXXXXX")
                it is IdOgType && (runCatching {
                    AktørId(it.brukerId.trim('"'))
                }.isSuccess || runCatching {
                    BrukerId(it.brukerId.trim('"'))
                }.isSuccess)
            }
            else -> false
        }
}