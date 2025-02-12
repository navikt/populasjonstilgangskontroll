package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EnhetsnummerTest {
  @Test
  @DisplayName("Gyldig Enhetsnummer skal opprettes uten problemer")
    fun gyldig() {
      assertEquals("1234", Enhetsnummer("1234").verdi)
    }

    @Test
    @DisplayName("Enhetsnummer med ugyldig lengde skal kaste IllegalArgumentException")
    fun feilLengde() {
        assertThrows<IllegalArgumentException> { Enhetsnummer("123") }
    }

    @Test
    @DisplayName("Enhetsnummer uten bare tall skal kaste IllegalArgumentException")
    fun ikkeBareTall() {
        assertThrows<IllegalArgumentException> { Enhetsnummer("12a4") }
    }
}