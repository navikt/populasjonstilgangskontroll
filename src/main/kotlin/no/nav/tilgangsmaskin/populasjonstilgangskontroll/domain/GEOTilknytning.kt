package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.neovisionaries.i18n.CountryCode
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain.GEOTilknytning.Type.*

sealed class GEOTilknytning(val type: Type) {
    enum class Type { BYDEL, KOMMUNE, UDEFINERT, UTLAND }

    @JvmInline
    value class Kommune(val verdi: String)  {
        init {
            require(verdi.length == 4) { "Ugyldig lengde ${verdi.length} for $verdi, forventet 4" }
            require(verdi.all { it.isDigit() }) { "Ugyldig(e) tegn i $verdi, forventet kun 4 tall" }
        }
    }

    @JvmInline
    value class Bydel(val verdi: String?)  {
        init {
            verdi?.let {
                require(it.length == 6) { "Ugyldig lengde ${it.length} for $it, forventet 6" }
                require(verdi.all { it.isDigit() }) { "Ugyldig(e) tegn i $verdi, forventet kun 6 tall" }
            }
        }
    }
    data class KommuneTilknytning(val kommune: Kommune) : GEOTilknytning(KOMMUNE)
    data class BydelTilknytning(val bydel: Bydel) : GEOTilknytning(BYDEL)
    class UdefinertTilknytning() : GEOTilknytning(UDEFINERT)
    data class UtenlandskTilknytning(val land: CountryCode) : GEOTilknytning(UTLAND)

    companion object  {
       val UDEFINERTTILKNYTNING = UdefinertTilknytning()
    }
}