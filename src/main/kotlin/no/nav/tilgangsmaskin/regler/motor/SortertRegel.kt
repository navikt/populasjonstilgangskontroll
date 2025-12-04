package no.nav.tilgangsmaskin.regler.motor

import org.springframework.core.annotation.AliasFor
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@Target(CLASS)
@Retention(RUNTIME)
@Order
@Component
annotation class SortertRegel(@get:AliasFor(annotation = Order::class, attribute = "value") val value: Int)