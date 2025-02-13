package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.neovisionaries.i18n.CountryCode
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GEOTilknytning.Type.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.requires

sealed class GEOTilknytning(val type: Type) {
    enum class Type { BYDEL, KOMMUNE, UDEFINERT, UTLAND }

    @JvmInline
    value class Kommune(val verdi: String)  {
        init {
            requires(verdi,4)
        }
    }

    @JvmInline
    value class Bydel(val verdi: String)  {
        init {
           requires(verdi,6)
        }
    }
    data class KommuneTilknytning(val kommune: Kommune) : GEOTilknytning(KOMMUNE)
    data class BydelTilknytning(val bydel: Bydel) : GEOTilknytning(BYDEL)
    data class UtenlandskTilknytning(val land: CountryCode) : GEOTilknytning(UTLAND)
    class UdefinertTilknytning : GEOTilknytning(UDEFINERT)

    companion object  {
       val UdefinertGeoTilknytning = UdefinertTilknytning()
    }
}

