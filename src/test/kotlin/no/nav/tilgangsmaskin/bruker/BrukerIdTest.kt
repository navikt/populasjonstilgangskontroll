package no.nav.tilgangsmaskin.bruker

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
class BrukerIdTest {

    @Test
    @DisplayName("Gyldig Fødselsnummer skal opprettes uten problemer")
    fun ok() {
            BrukerId("08526835671")
        }


    @Test
    @DisplayName("Fødselsnummer med ugyldig lengde skal kaste IllegalArgumentException")
    fun ikke11Tall() {
        assertThrows<IllegalArgumentException> { BrukerId("111") }
    }

    @Test
    @DisplayName("Fødselsnummer uten bare tall skal kaste IllegalArgumentException")
    fun ikkeBareTall() {
        assertThrows<IllegalArgumentException> { BrukerId("1111111111a") }
    }
}