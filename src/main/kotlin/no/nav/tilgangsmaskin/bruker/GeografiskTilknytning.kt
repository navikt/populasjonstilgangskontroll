package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.BYDEL
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.KOMMUNE
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.UDEFINERT
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.UKJENT_BOSTED
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.UTLAND
import no.nav.tilgangsmaskin.felles.rest.cache.JsonCacheable
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

    class UtenlandskTilknytning : GeografiskTilknytning(UTLAND){
        override fun equals(other: Any?) = other is UtenlandskTilknytning
        override fun hashCode() = javaClass.hashCode()
    }
    class UdefinertTilknytning : GeografiskTilknytning(UDEFINERT) {
        override fun equals(other: Any?) = other is UdefinertTilknytning
        override fun hashCode() = javaClass.hashCode()
    }
}