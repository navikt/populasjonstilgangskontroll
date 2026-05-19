package no.nav.tilgangsmaskin.bruker

import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.BYDEL
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.KOMMUNE
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.UDEFINERT
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.UKJENT_BOSTED
import no.nav.tilgangsmaskin.bruker.GeografiskTilknytning.Type.UTLAND
import no.nav.tilgangsmaskin.felles.NoCoverageAnalysis
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

    data class KommuneTilknytning(val kommune: Kommune) : GeografiskTilknytning(KOMMUNE) {
        @NoCoverageAnalysis
        override fun toString() = "${javaClass.simpleName} (kommune=${kommune.verdi})"
    }

    data class BydelTilknytning(val bydel: Bydel) : GeografiskTilknytning(BYDEL) {
        @NoCoverageAnalysis
        override fun toString() = "${javaClass.simpleName} (bydel=${bydel.verdi})"
    }

    class UkjentBosted : GeografiskTilknytning(UKJENT_BOSTED) {
        @NoCoverageAnalysis
        override fun equals(other: Any?) = other is UkjentBosted
        @NoCoverageAnalysis
        override fun hashCode() = javaClass.hashCode()
    }

    class UtenlandskTilknytning : GeografiskTilknytning(UTLAND) {
        @NoCoverageAnalysis
        override fun equals(other: Any?) = other is UtenlandskTilknytning
        @NoCoverageAnalysis
        override fun hashCode() = javaClass.hashCode()
    }

    class UdefinertTilknytning : GeografiskTilknytning(UDEFINERT) {
        @NoCoverageAnalysis
        override fun equals(other: Any?) = other is UdefinertTilknytning
        @NoCoverageAnalysis
        override fun hashCode() = javaClass.hashCode()
    }
}