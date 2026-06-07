package no.nav.tilgangsmaskin.tilgang

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import no.nav.tilgangsmaskin.regler.motor.BrukerIdOgRegelsett
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE
import org.springframework.web.server.ResponseStatusException
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [BulkGyldigValidator::class])
@Target(VALUE_PARAMETER)
@Retention(RUNTIME)
annotation class BulkGyldig(
    val message: String = "Bulk-forespørsel er ugyldig",
    val max: Int = 1000,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [])

class BulkGyldigValidator : ConstraintValidator<BulkGyldig, Set<BrukerIdOgRegelsett>> {

    private var max: Int = DEFAULT_MAX

    override fun initialize(ann: BulkGyldig) {
        max = ann.max
    }

    override fun isValid(value: Set<BrukerIdOgRegelsett>?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return true
        valider(value, max)
        return true
    }

    companion object {
        private const val DEFAULT_MAX = 1000

        fun valider(specs: Set<BrukerIdOgRegelsett>, max: Int = DEFAULT_MAX) {
            if (specs.size > max) {
                throw ResponseStatusException(PAYLOAD_TOO_LARGE, "Maksimalt $max brukerId-er kan sendes i en bulk forespørsel")
            }
            if (specs.any { it.brukerId.isBlank() }) {
                throw ResponseStatusException(BAD_REQUEST, "brukerId kan ikke være tom")
            }
        }
    }
}



