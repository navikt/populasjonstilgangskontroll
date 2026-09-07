package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import org.slf4j.LoggerFactory.getLogger
import org.springframework.core.annotation.AliasFor
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

interface Regel {
    fun evaluer(ansatt: Ansatt, bruker: Bruker): Boolean
    val metadata: RegelMetadata
    val kode: String
        get() = metadata.kode
    val kortNavn: String
        get() = metadata.kortNavn
    val begrunnelse: String
        get() = metadata.begrunnelse
    val erOverstyrbar: Boolean
        get() = this is OverstyrbarRegel
    fun godtaHvis(predikat: () -> Boolean) = predikat()
    fun avvisHvis(predikat: () -> Boolean) = !godtaHvis(predikat)

    val log: org.slf4j.Logger
        get() = getLogger(javaClass)

}

interface OverstyrbarRegel : Regel
interface KjerneRegel : Regel

@Target(CLASS)
@Retention(RUNTIME)
@Order
@Component
annotation class SortertRegel(@get:AliasFor(annotation = Order::class, attribute = "value") val value: Int)