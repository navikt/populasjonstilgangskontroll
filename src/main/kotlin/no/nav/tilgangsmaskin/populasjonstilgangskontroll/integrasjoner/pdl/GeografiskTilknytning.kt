package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.pdl

import com.neovisionaries.i18n.CountryCode.*

data class GTRespons(
    val gtType: GTType,
    val gtKommune: GTKommune?,
    val gtBydel: GTBydel?,
    val gtLand: GTLand?
)
{
    enum class GTType { BYDEL, KOMMUNE, UDEFINERT, UTLAND }

    @JvmInline
    value class GTKommune(val value: String)

    @JvmInline
    value class GTBydel(val value: String)

    data class GTLand(private val alpha3: String) {
        val gtLand = getByAlpha3Code(alpha3)
    }
}



