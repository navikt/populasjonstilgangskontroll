package no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker

import com.neovisionaries.i18n.CountryCode
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.extensions.DomainExtensions.requireDigits
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.bruker.GeografiskTilknytning.Type.*
sealed class GeografiskTilknytning(val type: Type) {
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
    data class KommuneTilknytning(val kommune: Kommune) : GeografiskTilknytning(KOMMUNE)
    data class BydelTilknytning(val bydel: Bydel) : GeografiskTilknytning(BYDEL)
    class UkjentBosted : GeografiskTilknytning(UKJENT_BOSTED)
    data class UtenlandskTilknytning(val land: CountryCode) : GeografiskTilknytning(UTLAND)
    class UdefinertTilknytning : GeografiskTilknytning(UDEFINERT)

    companion object  {
       val UdefinertGeoTilknytning = UdefinertTilknytning()
    }
}