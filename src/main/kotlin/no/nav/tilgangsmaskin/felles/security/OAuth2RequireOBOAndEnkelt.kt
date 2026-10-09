package no.nav.tilgangsmaskin.felles.security

import no.nav.sikkerhetstjenesten.felles.security.OBO_AUTHORITY
import org.springframework.security.access.prepost.PreAuthorize
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
@Target(CLASS, FUNCTION)
@Retention(RUNTIME)
@PreAuthorize("hasAuthority('$OBO_AUTHORITY') and hasRole('$ENKELT')")
annotation class RequireOAuth2OBOAndEnkelt