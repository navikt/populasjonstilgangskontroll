package no.nav.tilgangsmaskin.felles

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.Locale

class FellesBeanConfigMessageSourceTest : BehaviorSpec({

    Given("messageSource auto-konfigurert med spring.messages.basename") {
        When("en kjent message key finnes") {
            Then("resolves key til forventet tekst") {
                val source = ResourceBundleMessageSource().apply {
                    setBasenames("messages")
                    setDefaultEncoding("UTF-8")
                }
                val key = "problemDetail.no.nav.tilgangsmaskin.populasjonstilgangskontroll.regelmotor.RegelException.kjerneregler"

                val resolved = source.getMessage(
                    key,
                    arrayOf("TESTREGEL", "Z123456", "12345678901"),
                    Locale.getDefault()
                )

                resolved shouldBe "Kjerneregel TESTREGEL er ikke overstyrbar, kan ikke overstyre tilgang for ansatt Z123456 til bruker 12345678901"
            }
        }
    }
})
