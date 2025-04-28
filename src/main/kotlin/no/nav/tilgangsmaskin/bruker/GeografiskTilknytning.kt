package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.BYDEL
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.KOMMUNE
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.UDEFINERT
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.UKJENT_BOSTED
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.UTLAND
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.requireDigits


sealed class GeografiskTilknytning(type: Type) {
    enum class Type { BYDEL, KOMMUNE, UDEFINERT, UTLAND, UKJENT_BOSTED }

    @JvmInline
    value class Kommune(val verdi: String) {
        init {
            requireDigits(verdi, 4)
        }
    }

    @JvmInline
    value class Bydel(val verdi: String) {
        init {
            requireDigits(verdi, 6)
        }
    }

    data class KommuneTilknytning(val kommune: Kommune) : GeografiskTilknytning(KOMMUNE)
    data class BydelTilknytning(val bydel: Bydel) : GeografiskTilknytning(BYDEL)
    class UkjentBosted : GeografiskTilknytning(UKJENT_BOSTED)
    class UtenlandskTilknytning : GeografiskTilknytning(UTLAND)
    class UdefinertTilknytning : GeografiskTilknytning(UDEFINERT)

    companion object {
        val ukjentBsted = UkjentBosted()
        val udefinertTilknytning = UdefinertTilknytning()
        val utenlandskTilknytning = UtenlandskTilknytning()
    }
}