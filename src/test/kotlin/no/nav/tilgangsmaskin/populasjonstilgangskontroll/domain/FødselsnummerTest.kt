package no.nav.tilgangsmaskin.populasjonstilgangskontroll.domain

import com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.NAIS_CLUSTER_NAME
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.PROD_GCP
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FødselsnummerTest {

    @Test
    @DisplayName("Gyldig Fødselsnummer skal opprettes uten problemer")
    fun ok() {
        withEnvironmentVariable(NAIS_CLUSTER_NAME, PROD_GCP).execute {
            BrukerId("08526835671")
        }
    }
    @Test
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
}