package no.nav.tilgangsmaskin.bruker.pdl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonValue

@JsonIgnoreProperties(ignoreUnknown = true)
data class PdlGeografiskTilknytning(
        val gtType: GTType,
        val gtKommune: GTKommune? = null,
        val gtBydel: GTBydel? = null,
        val gtLand: GTLand? = null
) {
    enum class GTType { BYDEL, KOMMUNE, UDEFINERT, UTLAND }

    data class GTKommune(@JsonValue val verdi: String)

    data class GTBydel(@JsonValue val verdi: String)

    data class GTLand(@JsonValue val verdi: String)
}



