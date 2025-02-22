package no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.overstyring

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.TestApp
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.AnsattTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.BrukerTjeneste
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.fnr
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.motor
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.navid
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.vanligAnsatt
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regler.TestData.vanligBruker
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.TEST
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.Instant
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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

    @Autowired
    lateinit var entityManager: TestEntityManager


    @Test
    fun testNyeste() {
        /*
        val entity = OverstyringEntity(navid.verdi, fnr.verdi, "gammel", Instant.now(),Instant.now(),Instant.now())
        val entity1 = OverstyringEntity(navid.verdi, fnr.verdi, "nyere", Instant.now().plusSeconds(42),Instant.now(),Instant.now())
        entityManager.persist(entity)
        entityManager.persist(entity1)
        val found = repo.findLatest(navid.verdi, fnr.verdi)
        assertNotNull(found)
        assertEquals("nyere", found.begrunnelse)

         */
        val overstyring = OverstyringTjeneste(ansatt, bruker, OverstyringJPAAdapter(repo), motor)
        every { ansatt.ansatt(navid) } returns vanligAnsatt
        every { bruker.bruker(fnr) } returns vanligBruker
        overstyring.overstyr(navid, fnr, OverstyringMetadata("nyest", LocalDate.now().minusDays(1)))
        assertFalse(overstyring.erOverstyrt(navid, fnr))
        overstyring.overstyr(navid, fnr, OverstyringMetadata("nyest", LocalDate.now().plusDays(1)))
        assertTrue(overstyring.erOverstyrt(navid, fnr))
        //repo.delete(found)
    }
}