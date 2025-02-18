package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NavIdTest {
    @Test
    @DisplayName("Ugyldig NavId OK")
    fun validNavId() {
        assertEquals("A123456", NavId("A123456").verdi)
    }

    @Test
    @DisplayName("NavId med ugyldig lengde skal kaste IllegalArgumentException")
    fun lengde() {
        assertThrows<IllegalArgumentException> { NavId("A12345") }
    }

    @Test
    @DisplayName("NavId uten stor bokstav først skal kaste IllegalArgumentException")
    fun ikkeStprBokstav() {
        assertThrows<IllegalArgumentException> { NavId("a123456") }
    }

    @Test
    @DisplayName("NavId uten 6 tall etter første bokstav skal kaste IllegalArgumentException")
    fun ikke6tall() {
        assertThrows<IllegalArgumentException> { NavId("A12345a") }
    }
}