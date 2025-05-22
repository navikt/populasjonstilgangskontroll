package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.annotation.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class PdlGeografiskTilknytning(
        val gtType: GTType,
        val gtKommune: GTKommune? = null,
        val gtBydel: GTBydel? = null,
        val gtLand: GTLand? = null
) {
    enum class GTType { BYDEL, KOMMUNE, UDEFINERT, UTLAND }

    //@JvmInline
    data class GTKommune(val verdi: String)

    //@JvmInline
    data class GTBydel(val verdi: String)

   // @JvmInline
   data class GTLand(@JsonValue val verdi: String)
}



