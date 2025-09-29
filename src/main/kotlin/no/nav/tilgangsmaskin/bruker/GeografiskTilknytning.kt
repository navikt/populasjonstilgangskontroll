package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.*
import no.nav.tilgangsmaskin.felles.cache.JsonCacheable
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.requireDigits


@JsonCacheable
sealed class GeografiskTilknytning(type: Type) {
    enum class Type { BYDEL, KOMMUNE, UDEFINERT, UTLAND, UKJENT_BOSTED }

    @JsonCacheable
    data class Kommune(val verdi: String) {
        init {
            requireDigits(verdi, 4)
        }
    }

    @JsonCacheable
    data class Bydel(val verdi: String) {
        init {
            requireDigits(verdi, 6)
        }
    }

    @JsonCacheable
    data class KommuneTilknytning(val kommune: Kommune) : GeografiskTilknytning(KOMMUNE)
    @JsonCacheable
    data class BydelTilknytning(val bydel: Bydel) : GeografiskTilknytning(BYDEL)
    @JsonCacheable
    class UkjentBosted : GeografiskTilknytning(UKJENT_BOSTED) {
        override fun equals(other: Any?) = other is UkjentBosted
        override fun hashCode() = javaClass.hashCode()
    }

    @JsonCacheable
    class UtenlandskTilknytning : GeografiskTilknytning(UTLAND){
        override fun equals(other: Any?) = other is UtenlandskTilknytning
        override fun hashCode() = javaClass.hashCode()
    }
    @JsonCacheable
    class UdefinertTilknytning : GeografiskTilknytning(UDEFINERT) {
        override fun equals(other: Any?) = other is UdefinertTilknytning
        override fun hashCode() = javaClass.hashCode()
    }
}