package no.nav.tilgangsmaskin.regler.enkelttilgang

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions.isBetween
import java.time.LocalDate.now

class EnkeltTilgangValidator : ConstraintValidator<EnkeltTilgangGyldig, EnkeltTilgangData> {

    private var months: Long = 3
    private var min: Int = 10
    private var max: Int = 255

    override fun initialize(ann: EnkeltTilgangGyldig) {
        months = ann.months
        min = ann.min
        max = ann.max
    }

    override fun isValid(verdi: EnkeltTilgangData, context: ConstraintValidatorContext) =
        with(now()) {
            verdi.gyldigtil.isBetween(this, plusMonths(months)) &&
                    verdi.begrunnelse.length in min..max
        }
}