package no.nav.tilgangsmaskin.ansatt.nom

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import java.time.LocalDate.EPOCH
import java.time.LocalDate.now
import no.nav.tilgangsmaskin.TestApp
import no.nav.tilgangsmaskin.ansatt.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import no.nav.tilgangsmaskin.regler.AnsattBuilder
import no.nav.tilgangsmaskin.regler.BrukerBuilder
import no.nav.tilgangsmaskin.regler.brukerids
import no.nav.tilgangsmaskin.regler.grupper
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringEntityListener
import no.nav.tilgangsmaskin.tilgang.TokenClaimsAccessor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.BeforeTest
import kotlin.test.Test


@DataJpaTest
@ContextConfiguration(classes = [NomJPAAdapter::class, OverstyringEntityListener::class, TestApp::class])
@ExtendWith(MockKExtension::class)
@ActiveProfiles(TEST)
@Transactional
@Testcontainers
internal class NomTest {

    private val IGÅR = NomAnsattPeriode(EPOCH, now().minusDays(1))
    private val UTGÅTT = NomAnsattData(
            AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId,
            BrukerBuilder(brukerids.vanligBrukerId).build().brukerId, IGÅR)
    private val GYLDIG = NomAnsattData(
            AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId,
            BrukerBuilder(brukerids.vanligBrukerId).build().brukerId)

    @Autowired
    private lateinit var adapter: NomJPAAdapter

    @MockkBean
    private lateinit var accessor: TokenClaimsAccessor

    private lateinit var nom: Nom

    @BeforeTest
    fun setup() {
        every { accessor.system } returns "test"
        nom = Nom(adapter)
    }

    @Test
    @DisplayName("Test utgått ansatt")
    fun ansattIkkeLengerAnsatt() {
        nom.lagre(UTGÅTT)
        assertThat(nom.fnrForAnsatt(AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId)).isNull()
    }

    @Test
    @DisplayName("Test ingen sluttdato ok")
    fun ingenSluttdato() {
        nom.lagre(GYLDIG)
        assertThat(
                nom.fnrForAnsatt(
                        AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId)).isEqualTo(GYLDIG.brukerId)
    }

    @Test
    @DisplayName("Test lagre, så oppdater, siste gjelder")
    fun oppdaterSamme() {
        nom.lagre(UTGÅTT)
        assertThat(nom.fnrForAnsatt(AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId)).isNull()
        nom.lagre(GYLDIG)
        assertThat(
                nom.fnrForAnsatt(
                        AnsattBuilder().grupper(grupper.annenGruppe).build().ansattId)).isEqualTo(GYLDIG.brukerId)
    }

    companion object {
        @ServiceConnection
        private val postgres = PostgreSQLContainer("postgres:17")
    }
}