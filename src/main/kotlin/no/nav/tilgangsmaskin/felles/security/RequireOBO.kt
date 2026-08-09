package no.nav.tilgangsmaskin.felles.security

import org.springframework.security.access.prepost.PreAuthorize
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(CLASS, FUNCTION)
@Retention(RUNTIME)
@PreAuthorize("@tokenTypeAuthorization.requireOBO()")
annotation class RequireOBO
