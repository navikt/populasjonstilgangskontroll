package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import java.util.*

interface Regel {
    fun erOK(ansatt: Ansatt, bruker: Bruker): Boolean
    val metadata: RegelBeskrivelse
    val kode get() = metadata.kode
    val kortNavn get() = metadata.kortNavn
    val avvisningTekst get() = kode.årsak
    val erOverstyrbar get() = this is OverstyrbarRegel

    fun sjekkGruppeRegel(predicate: () -> Boolean, ansatt: Ansatt, id: UUID) =
        if (predicate.invoke()) ansatt kanBehandle id else true

    fun avslåHvis(predicate: () -> Boolean, teller: HabilitetsTeller, ok: Boolean = false) =
        if (predicate.invoke()) teller.registrerOppslag(ok) else true

}
