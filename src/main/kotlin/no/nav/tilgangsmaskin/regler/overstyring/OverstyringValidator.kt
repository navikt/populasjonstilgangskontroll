package no.nav.tilgangsmaskin.regler.overstyring

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.isBetween
import java.time.LocalDate.now

class OverstyringValidator : ConstraintValidator<ValidOverstyring, OverstyringData> {

    private var months: Long = 3
    private var min: Int = 10
    private var max: Int = 255

    override fun initialize(ann: ValidOverstyring) {
        months = ann.months
        min = ann.min
        max = ann.max
    }

    override fun isValid(verdi: OverstyringData, context: ConstraintValidatorContext) =
        with(now()) {
            verdi.gyldigtil.isBetween(this, plusMonths(months)) &&
                    verdi.begrunnelse.length in min..max
        }
}