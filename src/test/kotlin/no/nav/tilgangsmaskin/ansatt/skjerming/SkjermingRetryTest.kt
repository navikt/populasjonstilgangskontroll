package no.nav.tilgangsmaskin.ansatt.skjerming

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import java.net.URI
import no.nav.tilgangsmaskin.bruker.BrukerId
import no.nav.tilgangsmaskin.felles.rest.RecoverableRestException
import no.nav.tilgangsmaskin.felles.utils.cluster.ClusterConstants.TEST
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.resilience.annotation.EnableResilientMethods
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import kotlin.test.Test

@ContextConfiguration(classes = [SkjermingTjeneste::class])
@ExtendWith(MockKExtension::class)
@EnableResilientMethods
@ActiveProfiles(TEST)
@SpringBootTest
internal class SkjermingRetryTest {

    private val brukerId = BrukerId("08526835670")


    private val uri = URI.create("https://www.vg.no")

    @MockkBean
    lateinit var adapter: SkjermingRestClientAdapter

    @Autowired
    lateinit var tjeneste: SkjermingTjeneste

    @Test
    @DisplayName("Returner true etter at antall forsøk er oppbrukt")
    fun feilerEtterFireMislykkedeForsøk() {
        every { adapter.skjerming(brukerId.verdi) } throws RecoverableRestException(INTERNAL_SERVER_ERROR, uri)
        assertThrows<RecoverableRestException> {
            tjeneste.skjerming(brukerId)
        }
        verify(exactly = 4) {
            tjeneste.skjerming(brukerId)
        }
    }

    @Test
    @DisplayName("Test retry tar seg inn etter først å ha feilet")
    fun testRetryOK() {
        every { adapter.skjerming(brukerId.verdi) } throws RecoverableRestException(INTERNAL_SERVER_ERROR, uri) andThen false
        assertThat(tjeneste.skjerming(brukerId)).isFalse
        verify(exactly = 2) {
            tjeneste.skjerming(brukerId)
        }
    }

    @Test
    @DisplayName("Andre exceptions fører ikke til retry, og kastes umiddlelbart videre")
    fun andreExceptions() {
        every { adapter.skjerming(brukerId.verdi) } throws RuntimeException()
        assertThrows<RuntimeException> {
            tjeneste.skjerming(brukerId)
        }
        verify {
            tjeneste.skjerming(brukerId)
        }
    }
}
