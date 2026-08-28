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

    override fun isValid(verdi: EnkeltTilgangData, context: ConstraintValidatorContext): Boolean {
        var valid = true

        with(now()) {
            if (!verdi.gyldigtil.isBetween(now(), plusMonths(months))) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate(
                    "Gyldig til-dato må være fra ${now().toString()} og maks ${plusMonths(months).toString()}"
                )
                    .addPropertyNode("gyldigtil")
                    .addConstraintViolation()
                valid = false
            }

            if (verdi.begrunnelse.length !in min..max) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate(
                    "Begrunnelse må være mellom $min og $max tegn"
                )
                    .addPropertyNode("begrunnelse")
                    .addConstraintViolation()
                valid = false
            }
        }

        return valid
    }
}