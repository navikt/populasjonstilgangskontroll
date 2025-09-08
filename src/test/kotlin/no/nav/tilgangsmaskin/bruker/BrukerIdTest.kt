package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.EVERYTHING
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID


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

    @Test
    fun jalla() {
        val json = "[\"java.util.UUID\",\"3454c8df-a65a-4a0b-9390-1741395f9c78\"]"

        val mapper = jacksonObjectMapper().apply {
            activateDefaultTyping(polymorphicTypeValidator, EVERYTHING, PROPERTY)
        }

        val uuid = mapper.readValue(json, UUID::class.java)
        println(uuid)
    }
}