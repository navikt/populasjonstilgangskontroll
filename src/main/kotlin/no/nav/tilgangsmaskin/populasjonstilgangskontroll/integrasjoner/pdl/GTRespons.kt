package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.neovisionaries.i18n.CountryCode.getByAlpha3Code

data class GTRespons(
    val gtType: GTType,
    val gtKommune: GTKommune?,
    val gtBydel: GTBydel?,
    val gtLand: GTLand? = null)
{
    enum class GTType { BYDEL, KOMMUNE, UDEFINERT, UTLAND }

    @JvmInline
    value class GTKommune(val verdi: String)

    @JvmInline
    value class GTBydel(val verdi: String)

    @JvmInline
    value  class GTLand(val verdi: String)
}



