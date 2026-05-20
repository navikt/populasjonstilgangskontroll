package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.ansatt.GlobalGruppe
import no.nav.tilgangsmaskin.bruker.Bruker

abstract class GlobalGruppeMedlemskapRegel(private val gruppe: GlobalGruppe) : Regel {

    override fun evaluer(ansatt: Ansatt, bruker: Bruker) =
        avvisHvis { bruker kreverMedlemskapI gruppe && ansatt ikkeErMedlemAv gruppe }

    override val metadata = RegelMetadata(gruppe)

}