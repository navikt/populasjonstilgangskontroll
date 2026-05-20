package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Tag
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.UTILGJENGELIG
import org.springframework.core.annotation.AliasFor
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

interface Regel {
    fun evaluer(ansatt: Ansatt, bruker: Bruker): Boolean
    val metadata: RegelMetadata
    val kode get() = metadata.kode
    val kortNavn get() = metadata.kortNavn
    val begrunnelse get() = metadata.begrunnelse
    val erOverstyrbar get() = this is OverstyrbarRegel
    fun godtaHvis(predikat: () -> Boolean) = predikat.invoke()
    fun avvisHvis(predikat: () -> Boolean) = !godtaHvis(predikat)


    companion object {
        private const val REGEL = "regel"
        fun regelTag(regel: Regel) = Tag.of(REGEL, regel.kortNavn)
        val INGEN_REGEL_TAG = Tag.of(REGEL, UTILGJENGELIG)
    }
}

interface OverstyrbarRegel : Regel
interface KjerneRegel : Regel
interface TellendeRegel : Regel {
    val skalTelle: (Ansatt, Bruker) -> Boolean
        get() = { _, _ -> false }
    fun tell(ansatt: Ansatt, bruker: Bruker) = Unit

    override fun evaluer(ansatt: Ansatt, bruker: Bruker): Boolean {
        if (skalTelle(ansatt, bruker)) {
            tell(ansatt, bruker)
        }
        return true
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Order
@Component
annotation class SortertRegel(@get:AliasFor(annotation = Order::class, attribute = "value") val value: Int)