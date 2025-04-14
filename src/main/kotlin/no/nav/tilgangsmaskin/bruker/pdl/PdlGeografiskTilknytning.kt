package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PdlGeografiskTilknytning(
    val gtType: GTType,
    val gtKommune: GTKommune? = null,
    val gtBydel: GTBydel? = null,
    val gtLand: GTLand? = null
) {
    enum class GTType { BYDEL, KOMMUNE, UDEFINERT, UTLAND }

    @JvmInline
    value class GTKommune(val verdi: String)

    @JvmInline
    value class GTBydel(val verdi: String)

    @JvmInline
    value class GTLand(val verdi: String)
}



