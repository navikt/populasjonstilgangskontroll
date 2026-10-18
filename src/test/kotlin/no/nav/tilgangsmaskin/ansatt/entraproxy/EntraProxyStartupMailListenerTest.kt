package no.nav.tilgangsmaskin.ansatt.entraproxy

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.mail.SimpleMailMessage

class EntraProxyStartupMailListenerTest {
    private val entraProxyTjeneste = mockk<EntraProxyTjeneste>(relaxed = true)
    private val listener = EntraProxyStartupMailListener(entraProxyTjeneste)

    @Test
    fun `sends mail when application is ready`() {
        listener.sendStartupMail()

        val message = slot<SimpleMailMessage>()
        verify(exactly = 1) { entraProxyTjeneste.mail(capture(message)) }
        message.captured.getTo()?.toList() shouldBe listOf("jan-olav.eide@nav.no")
        message.captured.subject shouldBe "Populasjonstilgangskontroll startet"
        message.captured.text shouldBe "Populasjonstilgangskontroll startet."
    }
}
