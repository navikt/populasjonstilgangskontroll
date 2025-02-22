package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestApp
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.motor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.ukjentBostedBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.TEST
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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
@ContextConfiguration(classes= [TestApp::class])
@ExtendWith(MockKExtension::class)
@EnableJpaAuditing
@ActiveProfiles(TEST)
internal class OverstyringTest {

    @MockK
    lateinit var ansatt: AnsattTjeneste
    @MockK
    lateinit var bruker: BrukerTjeneste
    @Autowired
    lateinit var repo: OverstyringRepository

    lateinit var overstyring: OverstyringTjeneste

    @BeforeTest
    fun setup() {
        every { ansatt.ansatt(vanligAnsatt.navId) } returns vanligAnsatt
        overstyring = OverstyringTjeneste(ansatt, bruker, OverstyringJPAAdapter(repo), motor)
    }

    @Test
    @DisplayName("Test gyldig overstyring")
    fun testOverstyringGyldig() {
        every { bruker.bruker(vanligBruker.ident) } returns vanligBruker
        overstyring.overstyr(vanligAnsatt.navId, vanligBruker.ident, OverstyringMetadata("nyest", LocalDate.now().plusDays(1)))
        assertTrue(overstyring.erOverstyrt(vanligAnsatt.navId, vanligBruker.ident))
    }
    @Test
    @DisplayName("Test utgått overstyring")
    fun testOverstyring() {
        every { bruker.bruker(vanligBruker.ident) } returns vanligBruker
        overstyring.overstyr(vanligAnsatt.navId, vanligBruker.ident, OverstyringMetadata("nyest", LocalDate.now().minusDays(1)))
        assertFalse(overstyring.erOverstyrt(vanligAnsatt.navId, vanligBruker.ident))

    }
    @Test
    @DisplayName("Test overstyring, intet db innslag")
    fun testOverstyringIntetDBInnslag() {
        every { bruker.bruker(ukjentBostedBruker.ident) } returns ukjentBostedBruker
        assertFalse(overstyring.erOverstyrt(vanligAnsatt.navId, ukjentBostedBruker.ident))
    }
}