package no.nav.tilgangsmaskin.felles.security

import no.nav.sikkerhetstjenesten.felles.security.OAuth2RequireOBO
import org.springframework.security.access.prepost.PreAuthorize
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(CLASS, FUNCTION)
@Retention(RUNTIME)
@OAuth2RequireOBO
@PreAuthorize("hasRole('$ENKELT')")
annotation class OAuth2RequireOBOAndEnkelt
