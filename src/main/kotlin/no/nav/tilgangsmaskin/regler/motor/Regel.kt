package no.nav.tilgangsmaskin.regler.motor

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
    fun godtaHvis(predicate: () -> Boolean) = predicate.invoke()
    fun avvisHvis(predicate: () -> Boolean) = !godtaHvis(predicate)
}

abstract class GlobalGruppeRegel(private val gruppe: GlobalGruppe) : Regel {

    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { bruker kreverMedlemskapI gruppe && ansatt ikkeErMedlemAv gruppe }

    override val metadata = RegelMetadata(gruppe)

}