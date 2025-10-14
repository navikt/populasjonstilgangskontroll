package no.nav.tilgangsmaskin.felles.rest

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringData
import org.slf4j.LoggerFactory.getLogger
import java.time.LocalDate
import kotlin.reflect.KClass

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
