package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.overstyring

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestApp
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.motor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.ukjentBostedBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.TEST
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.TokenClaimsAccessor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test

@DataJpaTest(showSql = true)
@ContextConfiguration(classes = [TestApp::class])
@ExtendWith(MockKExtension::class)
@EnableJpaAuditing
@ActiveProfiles(TEST)
internal class OverstyringTest {

    @MockkBean
    lateinit var accessor: TokenClaimsAccessor
    @MockK
    lateinit var ansattTjeneste: AnsattTjeneste
    @MockK
    lateinit var brukerTjeneste: BrukerTjeneste
    @Autowired
    lateinit var repo: OverstyringRepository

    lateinit var overstyring: OverstyringTjeneste

    @BeforeTest
    fun setup() {
        every { accessor.system } returns "test"
        every { ansattTjeneste.ansatt(vanligAnsatt.ansattId) } returns vanligAnsatt
        overstyring = OverstyringTjeneste(ansattTjeneste, brukerTjeneste, OverstyringJPAAdapter(repo), motor)
    }

    @Test
    @DisplayName("Test gyldig overstyring")
    fun testOverstyringGyldig() {
        every { brukerTjeneste.bruker(vanligBruker.brukerId) } returns vanligBruker
        overstyring.overstyr(vanligAnsatt.ansattId, OverstyringData("gammel", LocalDate.now().minusDays(1),vanligBruker.brukerId))
        overstyring.overstyr(vanligAnsatt.ansattId,  OverstyringData("ny", LocalDate.now().plusDays(1),vanligBruker.brukerId,))
        assertThat(overstyring.erOverstyrt(vanligAnsatt.ansattId, vanligBruker.brukerId)).isTrue
    }
    @Test
    @DisplayName("Test utgått overstyring")
    fun testOverstyringUtgått() {
        every { brukerTjeneste.bruker(vanligBruker.brukerId) } returns vanligBruker
        overstyring.overstyr(vanligAnsatt.ansattId, OverstyringData("ny", LocalDate.now().minusDays(1),vanligBruker.brukerId))
        assertThat(overstyring.erOverstyrt(vanligAnsatt.ansattId, vanligBruker.brukerId)).isFalse

    }
    @Test
    @DisplayName("Test overstyring, intet db innslag")
    fun testOverstyringUtenDBInnslag() {
        every { brukerTjeneste.bruker(ukjentBostedBruker.brukerId) } returns ukjentBostedBruker
        assertThat(overstyring.erOverstyrt(vanligAnsatt.ansattId, ukjentBostedBruker.brukerId)).isFalse
    }
}