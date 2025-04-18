package no.nav.tilgangsmaskin.felles.utils.extensions

import no.nav.tilgangsmaskin.bruker.BrukerId


object DomainExtensions {
    fun requireDigits(verdi: String, len: Int): Unit {
        require(verdi.all { it.isDigit() }) { "Ugyldig(e) tegn i $verdi, forventet $len siffer" }
        require(verdi.length == len) { "Ugyldig lengde ${verdi.length} for $verdi, forventet $len siffer" }
    }

    fun BrukerId.maskFnr() = verdi.maskFnr()
    fun String.maskFnr() = if (length == 11) replaceRange(6, 11, "*****") else this
    fun String.pluralize(list: List<Any>) =
        if (list.size == 1) this else if (this.endsWith('e')) "${this}r" else "${this}er"

}