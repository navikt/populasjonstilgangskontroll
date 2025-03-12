package no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.skjerming

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.IrrecoverableRestException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.errors.RecoverableRestException
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.integrasjoner.felles.FellesRetryListener
import no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.TestData.vanligBruker

import no.nav.tilgangsmaskin.populasjonstilgangskontroll.utils.Constants.TEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.retry.annotation.EnableRetry
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.net.URI
import kotlin.test.Test

@ContextConfiguration(classes = [SkjermingTjeneste::class])
@ExtendWith(MockKExtension::class)
@EnableRetry
@ActiveProfiles(TEST)
@SpringBootTest(classes = [FellesRetryListener::class])
internal class SkjermingRetryTest {

    private val uri =  URI.create("https://www.vg.no")

    @MockkBean
    lateinit var adapter: SkjermingRestClientAdapter

    @Autowired
    lateinit  var tjeneste: SkjermingTjeneste

    @Test
    @DisplayName("Returner true etter at antall forsøk er oppbrukt")
    fun erSkjermetEtterTreMislykkedeForsøk() {
        every { adapter.erSkjermet(vanligBruker.brukerId.verdi) } throws RecoverableRestException(INTERNAL_SERVER_ERROR, uri)
        assertThat(tjeneste.erSkjermet(vanligBruker.brukerId)).isTrue
        verify(exactly = 3) {
            tjeneste.erSkjermet(vanligBruker.brukerId)
        }
    }

    @Test
    @DisplayName("Test retry tar seg inn etter først å ha feilet")
    fun testRetryOK() {
        every { adapter.erSkjermet(vanligBruker.brukerId.verdi) } throws RecoverableRestException(INTERNAL_SERVER_ERROR, uri) andThen false
        assertThat(tjeneste.erSkjermet(vanligBruker.brukerId)).isFalse
        verify(exactly = 2) {
            tjeneste.erSkjermet(vanligBruker.brukerId)
        }
    }
    @Test
    @DisplayName("Andre exceptions fører ikke til retry, og kastes umiddlelbart videre")
    fun andreExceptions() {
        every { adapter.erSkjermet(vanligBruker.brukerId.verdi) } throws IrrecoverableRestException(INTERNAL_SERVER_ERROR, uri)
        assertThrows<IrrecoverableRestException>  {
            tjeneste.erSkjermet(vanligBruker.brukerId)
        }
        verify(exactly = 1) {
            tjeneste.erSkjermet(vanligBruker.brukerId)
        }
    }
}