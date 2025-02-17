package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.neovisionaries.i18n.CountryCode
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GeoTilknytning.Type.*
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ObjectUtil.requires

sealed class GeoTilknytning(val type: Type) {
    enum class Type { BYDEL, KOMMUNE, UDEFINERT, UTLAND, UKJENT_BOSTED }

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
    data class KommuneTilknytning(val kommune: Kommune) : GeoTilknytning(KOMMUNE)
    data class BydelTilknytning(val bydel: Bydel) : GeoTilknytning(BYDEL)
    class UkjentBosted() : GeoTilknytning(UKJENT_BOSTED)
    data class UtenlandskTilknytning(val land: CountryCode) : GeoTilknytning(UTLAND)
    class UdefinertTilknytning : GeoTilknytning(UDEFINERT)

    companion object  {
       val UdefinertGeoTilknytning = UdefinertTilknytning()
    }
}

