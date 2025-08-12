package no.nav.tilgangsmaskin.felles.rest

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import no.nav.tilgangsmaskin.bruker.AktørId
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringData
import org.slf4j.LoggerFactory.getLogger
import java.time.LocalDate
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
            is Set<*> -> true /*verdi.all {
                it is IdOgType && (runCatching {
                    AktørId(it.brukerId.trim('"'))
                }.isSuccess || runCatching {
                    BrukerId(it.brukerId.trim('"'))
                }.isSuccess)
            }*/
            else -> false
        }
}

@MustBeDocumented
@Constraint(validatedBy = [OverstyringValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidOverstyring(
    val message: String = "Overstyring må være fra nå og maks 3 måneder frem i tid",
    val groups: Array<KClass<*>> = [],
    val months: Long = 3,
    val payload: Array<KClass<out Payload>> = [])

class OverstyringValidator : ConstraintValidator<ValidOverstyring, OverstyringData> {
    private val log = getLogger(javaClass)

    private var months: Long = 3
    override fun initialize(constraintAnnotation: ValidOverstyring) {
        months = constraintAnnotation.months
    }
    override fun isValid(verdi: OverstyringData, context: ConstraintValidatorContext) =
        gyldigDato(verdi.gyldigtil) && gyldigLengde(verdi.begrunnelse)

    private fun gyldigLengde(verdi: String) = (verdi.length in 10..255).also {
        if (!it) log.warn("Overstyring med begrunnelse '$verdi' er ugyldig, må være mellom 10 og 255 tegn")
    }

    private fun gyldigDato(verdi: LocalDate) =
        verdi.isAfter(LocalDate.now()) && verdi.isBefore(LocalDate.now().plusMonths(3))
            .also {
                if (!it) log.warn("Overstyring med gyldig til $verdi er ugyldig, må være fra nå og maks 3 måneder frem i tid")
            }
}
