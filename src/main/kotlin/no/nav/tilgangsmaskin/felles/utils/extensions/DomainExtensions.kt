package no.nav.tilgangsmaskin.felles.utils.extensions


object DomainExtensions {
    fun requireDigits(verdi: String, len: Int) {
        require(verdi.all { it.isDigit() }) { "Ugyldig(e) tegn i $verdi, forventet $len siffer" }
        require(verdi.length == len) { "Ugyldig lengde ${verdi.length} for $verdi, forventet $len siffer" }
    }

    fun String.upcase() = this.replaceFirstChar { it.uppercaseChar() }
    fun String.maskFnr() = if (length == 11) replaceRange(6, 11, "*****") else this
    fun String.pluralize(l: Collection<Any>, suffix: String? = "er") = if (l.size == 1) this else if (this.endsWith('e')) "${this}r" else "${this}er"

}