package no.nav.tilgangsmaskin.ansatt.nom

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import java.time.LocalDate.EPOCH
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode.Companion.ALWAYS
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.felles.utils.extensions.TimeExtensions
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringEntityListener
import no.nav.tilgangsmaskin.tilgang.Token
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.Test


@DataJpaTest
@ContextConfiguration(classes = [NomJPAAdapter::class, OverstyringEntityListener::class, TestApp::class])
@ExtendWith(MockKExtension::class)
@ActiveProfiles(TEST)
@Transactional
@Testcontainers
@TestInstance(PER_CLASS)
internal class NomTest {

    private val vanligBrukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")
    private val IGÅR = NomAnsattPeriode(EPOCH, TimeExtensions.IGÅR)
    private val UTGÅTT = NomAnsattData(ansattId, vanligBrukerId, this@NomTest.IGÅR)
    private val GYLDIG = NomAnsattData(ansattId, vanligBrukerId, ALWAYS)

    @Autowired
    private lateinit var adapter: NomJPAAdapter

    @MockkBean
    private lateinit var token: Token

    private lateinit var nom: NomTjeneste

    @BeforeAll
    fun setup() {
        every { token.system } returns "test"
        nom = NomTjeneste(adapter)
    }

    @Test
    @DisplayName("Utgått ansatt retureres ikke")
    fun ansattIkkeLengerAnsatt() {
        nom.lagre(UTGÅTT)
        assertThat(nom.fnrForAnsatt(ansattId)).isNull()
    }

    @Test
    @DisplayName("Ansatt uten sluttdato er gyldig")
    fun ingenSluttdato() {
        nom.lagre(GYLDIG)
        assertThat(nom.fnrForAnsatt(ansattId)).isEqualTo(GYLDIG.brukerId)
    }

    @Test
    @DisplayName("Siste hendelse gjelder")
    fun oppdaterSamme() {
        nom.lagre(UTGÅTT)
        assertThat(nom.fnrForAnsatt(ansattId)).isNull()
        nom.lagre(GYLDIG)
        assertThat(nom.fnrForAnsatt(ansattId)).isEqualTo(GYLDIG.brukerId)
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }
}