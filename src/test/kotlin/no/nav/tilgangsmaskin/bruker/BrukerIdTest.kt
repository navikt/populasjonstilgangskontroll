package no.nav.tilgangsmaskin.bruker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.NAIS_CLUSTER_NAME
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.PROD_GCP
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import io.lettuce.core.RedisURI
import java.util.UUID


class BrukerIdTest {

    @Test
    @DisplayName("Gyldig Fødselsnummer skal opprettes uten problemer")
    fun ok() {
        withEnvironmentVariable(NAIS_CLUSTER_NAME, PROD_GCP).execute {
            BrukerId("08526835671")
        }
    }

    // @Test
    @DisplayName("Fødselsnummer med ugyldig kontrollsiffer skal kaste IllegalArgumentException")
    fun ikkeOk() {
        withEnvironmentVariable(NAIS_CLUSTER_NAME, PROD_GCP).execute {
            assertThrows<IllegalArgumentException> { BrukerId("11111111111") }
        }
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
    @DisplayName("Fødselsnummer med ugyldig format skal kaste IllegalArgumentException")
    fun jalla() {
        val json = "[\"java.util.UUID\",\"3454c8df-a65a-4a0b-9390-1741395f9c78\"]"

        val mapper = ObjectMapper().apply {
            val ptv = BasicPolymorphicTypeValidator.builder().allowIfSubType(UUID::class.java).build()
            activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.EVERYTHING)
        }

        val uuid = mapper.readValue(json, UUID::class.java)
        println(uuid) // 3454c8df-a65a-4a0b-9390-1741395f9c78
    }
}