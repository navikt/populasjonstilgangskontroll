package no.nav.tilgangsmaskin.regler.overstyring

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.time.LocalDate
import java.time.LocalDate.now

class OverstyringValidator : ConstraintValidator<ValidOverstyring, OverstyringData> {

    private var months: Long = 3
    override fun initialize(constraintAnnotation: ValidOverstyring) {
        months = constraintAnnotation.months
    }
    override fun isValid(verdi: OverstyringData, context: ConstraintValidatorContext) =
        gyldigDato(verdi.gyldigtil) && gyldigLengde(verdi.begrunnelse)

    private fun gyldigLengde(verdi: String) =
        verdi.length in 10..400

    private fun gyldigDato(verdi: LocalDate) =
        verdi.isAfter(now()) && verdi.isBefore(now().plusMonths(months))
}