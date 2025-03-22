package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import com.neovisionaries.i18n.CountryCode
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil

sealed class GeoTilknytning(val type: Type) {
    enum class Type { BYDEL, KOMMUNE, UDEFINERT, UTLAND, UKJENT_BOSTED }

    @JvmInline
    value class Kommune(val verdi: String)  {
        init {
            ObjectUtil.requireDigits(verdi, 4)
        }
    }

    @JvmInline
    value class Bydel(val verdi: String)  {
        init {
            ObjectUtil.requireDigits(verdi, 6)
        }
    }
    data class KommuneTilknytning(val kommune: Kommune) : GeoTilknytning(Type.KOMMUNE)
    data class BydelTilknytning(val bydel: Bydel) : GeoTilknytning(Type.BYDEL)
    class UkjentBosted : GeoTilknytning(Type.UKJENT_BOSTED)
    data class UtenlandskTilknytning(val land: CountryCode) : GeoTilknytning(Type.UTLAND)
    class UdefinertTilknytning : GeoTilknytning(Type.UDEFINERT)

    companion object  {
       val UdefinertGeoTilknytning = UdefinertTilknytning()
    }
}