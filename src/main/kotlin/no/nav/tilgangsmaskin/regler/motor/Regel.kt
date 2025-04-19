package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.Bruker

interface Regel {
    fun evaluer(ansatt: Ansatt, bruker: Bruker): Boolean
    val metadata: Metadata
    val kode get() = metadata.kode
    val kortNavn get() = metadata.kortNavn
    val begrunnelse get() = metadata.begrunnelse
    val erOverstyrbar get() = this is OverstyrbarRegel


    fun avslåHvis(predicate: () -> Boolean) = !predicate.invoke()
}

abstract class GlobalGruppeRegel(private val gruppe: GlobalGruppe) : Regel {

    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avslåHvis { bruker krever gruppe && !(ansatt tilhørerGruppe gruppe) }

    override val metadata = Metadata(gruppe)

}