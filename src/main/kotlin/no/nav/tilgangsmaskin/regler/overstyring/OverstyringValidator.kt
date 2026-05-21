package no.nav.tilgangsmaskin.regler.overstyring

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.time.LocalDate.now

class OverstyringValidator : ConstraintValidator<ValidOverstyring, OverstyringData> {

    private var months: Long = 3
    private var minLengde: Int = 10
    private var maxLengde: Int = 255

    override fun initialize(ann: ValidOverstyring) {
        months = ann.months
        minLengde = ann.minLengde
        maxLengde = ann.maxLengde
    }

    override fun isValid(verdi: OverstyringData, context: ConstraintValidatorContext): Boolean {
        val idag = now()
        return verdi.gyldigtil.isAfter(idag) && verdi.gyldigtil.isBefore(idag.plusMonths(months))
                && verdi.begrunnelse.length in minLengde..maxLengde
    }
}