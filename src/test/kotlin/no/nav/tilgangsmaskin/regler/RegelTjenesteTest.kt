package no.nav.tilgangsmaskin.regler

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.tilgangsmaskin.ansatt.AnsattId
import no.nav.tilgangsmaskin.ansatt.AnsattTjeneste
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.bruker.BrukerTjeneste
import no.nav.tilgangsmaskin.felles.rest.NotFoundRestException
import no.nav.tilgangsmaskin.regler.motor.RegelMotor
import no.nav.tilgangsmaskin.regler.overstyring.OverstyringTjeneste
import no.nav.tilgangsmaskin.tilgang.RegelTjeneste
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.URI
import kotlin.test.BeforeTest

@ExtendWith(MockKExtension::class)
class RegelTjenesteTest {

    private val vanligBrukerId = BrukerId("08526835670")
    private val ansattId = AnsattId("Z999999")

    @MockK
    private lateinit var motor: RegelMotor
    @MockK
    private lateinit var brukere: BrukerTjeneste
    @MockK
    private lateinit var ansatte: AnsattTjeneste
    @MockK
    private lateinit var overstyring: OverstyringTjeneste
    private lateinit var regler: RegelTjeneste

    @BeforeTest
    fun before() {
        every { ansatte.ansatt(ansattId) } returns AnsattBuilder(ansattId).build()
        regler = RegelTjeneste(motor, brukere, ansatte, overstyring)
    }

    @Test
    @DisplayName("Verifiser at tilgang gis når bruker ikke finnes i PDL ved kjerneregelssjekk")
    fun brukerIkkeFunnetIPdlKjerneregler() {
        every {
            brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi)
        } throws NotFoundRestException(URI.create("http://pdl"))

        assertThatCode {
            regler.kjerneregler(ansattId, vanligBrukerId.verdi)
        }.doesNotThrowAnyException()
    }

    @Test
    @DisplayName("Verifiser at tilgang gis når bruker ikke finnes i PDL ved komplett regelsjekk")
    fun brukerIkkeFunnetIPdl() {
        every {
            brukere.brukerMedNærmesteFamilie(vanligBrukerId.verdi)
        } throws NotFoundRestException(URI.create("http://pdl"))

        assertThatCode {
            regler.kompletteRegler(ansattId, vanligBrukerId.verdi)
        }.doesNotThrowAnyException()
    }
}