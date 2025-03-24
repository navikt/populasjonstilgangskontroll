package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestApp
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring.OverstyringEntityListener
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.ClusterConstants.TEST
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.nom.NomAnsattData.NomAnsattPeriode
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.tilgang.TokenClaimsAccessor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate.now
import kotlin.test.BeforeTest
import kotlin.test.Test

@DataJpaTest
@ContextConfiguration(classes = [NomJPAAdapter::class,OverstyringEntityListener::class,TestApp::class])
@ExtendWith(MockKExtension::class)
@EnableJpaAuditing
@ActiveProfiles(TEST)
@Transactional
internal class NomTest {

    private val IGÅR = NomAnsattPeriode(now().minusDays(1))
    private val UTGÅTT = NomAnsattData(vanligAnsatt.ansattId, vanligBruker.brukerId, IGÅR)
    private val GYLDIG = NomAnsattData(vanligAnsatt.ansattId, vanligBruker.brukerId)

    @Autowired
    lateinit var repo: NomRepository

    @MockkBean
    lateinit var accessor: TokenClaimsAccessor

    lateinit var nom: NomOperasjoner

    @BeforeTest
    fun setup() {
        every { accessor.system } returns "test"
        nom = NomTjeneste(NomJPAAdapter(repo))
    }

    @Test
    @DisplayName("Test utgått ansatt")
    fun ansattIkkeLengerAnsatt() {
        nom.lagre(UTGÅTT)
        assertThat(nom.fnrForAnsatt(vanligAnsatt.ansattId)).isNull()
    }
    @Test
    @DisplayName("Test ingen sluttdato ok")
    fun ingenSluttdato() {
        nom.lagre(GYLDIG)
        assertThat(nom.fnrForAnsatt(vanligAnsatt.ansattId)).isEqualTo(GYLDIG.brukerId)
    }
    @Test
    @DisplayName("Test lagre, så oppdater, siste gjelder")
    fun oppdaterSamme() {
        nom.lagre(UTGÅTT)
        assertThat(nom.fnrForAnsatt(vanligAnsatt.ansattId)).isNull()
        nom.lagre(GYLDIG)
        assertThat(nom.fnrForAnsatt(vanligAnsatt.ansattId)).isEqualTo(GYLDIG.brukerId)
    }
}