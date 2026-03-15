package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.BYDEL
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.KOMMUNE
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.UDEFINERT
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.UKJENT_BOSTED
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.UTLAND
import no.nav.tilgangsmaskin.felles.Generated
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.requireDigits


sealed class GeografiskTilknytning(type: Type) {
    enum class Type { BYDEL, KOMMUNE, UDEFINERT, UTLAND, UKJENT_BOSTED }

    data class Kommune(val verdi: String) {
        init {
            requireDigits(verdi, 4)
        }
    }

    data class Bydel(val verdi: String) {
        init {
            requireDigits(verdi, 6)
        }
    }

    data class KommuneTilknytning(val kommune: Kommune) : GeografiskTilknytning(KOMMUNE){
        @Generated
        override fun toString() = "${javaClass.simpleName} (kommune=${kommune.verdi})"
    }
    data class BydelTilknytning(val bydel: Bydel) : GeografiskTilknytning(BYDEL) {
        @Generated
        override fun toString() = "${javaClass.simpleName} (bydel=${bydel.verdi})"
    }
    class UkjentBosted : GeografiskTilknytning(UKJENT_BOSTED) {
        @Generated override fun equals(other: Any?) = other is UkjentBosted
        @Generated override fun hashCode() = javaClass.hashCode()
    }

    class UtenlandskTilknytning : GeografiskTilknytning(UTLAND){
        @Generated override fun equals(other: Any?) = other is UtenlandskTilknytning
        @Generated override fun hashCode() = javaClass.hashCode()
    }
    class UdefinertTilknytning : GeografiskTilknytning(UDEFINERT) {
        @Generated override fun equals(other: Any?) = other is UdefinertTilknytning
        @Generated override fun hashCode() = javaClass.hashCode()
    }
}