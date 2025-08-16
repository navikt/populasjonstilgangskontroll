package no.nav.tilgangsmaskin.regler.motor

import io.micrometer.core.instrument.Tags
import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.Bruker

interface Regel {
    fun evaluer(ansatt: Ansatt, bruker: Bruker): Boolean
    val metadata: RegelMetadata
    val kode get() = metadata.kode
    val kortNavn get() = metadata.kortNavn
    val begrunnelse get() = metadata.begrunnelse
    val navn get() = metadata.navn
    val erOverstyrbar get() = this is OverstyrbarRegel

    fun avvisHvis(predicate: () -> Boolean) = !predicate.invoke()
}
interface TellendeRegel : Regel {
    val predikat: (Ansatt, Bruker) -> Boolean
    fun tell(ansatt: Ansatt, bruker: Bruker)

    override fun evaluer(ansatt: Ansatt, bruker: Bruker): Boolean {
        if (predikat(ansatt, bruker)) {
            tell(ansatt, bruker)
        }
        return true
    }
}

abstract class GlobalGruppeRegel(private val gruppe: GlobalGruppe) : Regel {

    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { bruker kreverMedlemskapI gruppe && !(ansatt erMedlemAv gruppe) }

    override val metadata = RegelMetadata(gruppe)

}