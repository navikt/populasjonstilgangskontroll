package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import com.neovisionaries.i18n.CountryCode
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.DomainExtensions.requireDigits
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeoTilknytning.Type.*
sealed class GeoTilknytning(val type: Type) {
    enum class Type { BYDEL, KOMMUNE, UDEFINERT, UTLAND, UKJENT_BOSTED }

    @JvmInline
    value class Kommune(val verdi: String)  {
        init {
            requireDigits(verdi, 4)
        }
    }

    @JvmInline
    value class Bydel(val verdi: String)  {
        init {
            requireDigits(verdi, 6)
        }
    }
    data class KommuneTilknytning(val kommune: Kommune) : GeoTilknytning(KOMMUNE)
    data class BydelTilknytning(val bydel: Bydel) : GeoTilknytning(BYDEL)
    class UkjentBosted : GeoTilknytning(UKJENT_BOSTED)
    data class UtenlandskTilknytning(val land: CountryCode) : GeoTilknytning(UTLAND)
    class UdefinertTilknytning : GeoTilknytning(UDEFINERT)

    companion object  {
       val UdefinertGeoTilknytning = UdefinertTilknytning()
    }
}