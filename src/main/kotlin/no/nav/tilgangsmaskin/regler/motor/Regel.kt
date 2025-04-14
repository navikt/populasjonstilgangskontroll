package no.nav.tilgangsmaskin.regler.motor

import no.nav.tilgangsmaskin.ansatt.Ansatt
import no.nav.tilgangsmaskin.bruker.Bruker
import java.util.*
import java.util.function.Predicate

interface Regel {
    fun erOK(ansatt: Ansatt, bruker: Bruker): Boolean
    val metadata: RegelBeskrivelse
    val kode get() = metadata.kode
    val kortNavn get() = metadata.kortNavn
    val avvisningTekst get() = kode.årsak
    val erOverstyrbar get() = this is OverstyrbarRegel

    fun sjekkRegel(predicate: Predicate<Bruker>, bruker: Bruker, ansatt: Ansatt, id: UUID) =
        if (predicate.test(bruker)) ansatt kanBehandle id else true

}
