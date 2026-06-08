package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
import no.nav.tilgangsmaskin.felles.utils.extensions.DomainExtensions.requireDigits


sealed class GeografiskTilknytning {

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

    data class KommuneTilknytning(val kommune: Kommune) : GeografiskTilknytning() {
        @NoCoverageAnalysis
        override fun toString() = "${javaClass.simpleName} (kommune=${kommune.verdi})"
    }

    data class BydelTilknytning(val bydel: Bydel) : GeografiskTilknytning() {
        @NoCoverageAnalysis
        override fun toString() = "${javaClass.simpleName} (bydel=${bydel.verdi})"
    }

    class UkjentBosted : GeografiskTilknytning() {
        @NoCoverageAnalysis
        override fun equals(other: Any?) = other is UkjentBosted
        @NoCoverageAnalysis
        override fun hashCode() = javaClass.hashCode()
    }

    class UtenlandskTilknytning : GeografiskTilknytning() {
        @NoCoverageAnalysis
        override fun equals(other: Any?) = other is UtenlandskTilknytning
        @NoCoverageAnalysis
        override fun hashCode() = javaClass.hashCode()
    }

    class UdefinertTilknytning : GeografiskTilknytning() {
        @NoCoverageAnalysis
        override fun equals(other: Any?) = other is UdefinertTilknytning
        @NoCoverageAnalysis
        override fun hashCode() = javaClass.hashCode()
    }
}